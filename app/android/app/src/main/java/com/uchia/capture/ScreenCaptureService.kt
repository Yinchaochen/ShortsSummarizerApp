package com.uchia.capture

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.*
import android.provider.Settings
import android.util.Log

/**
 * Foreground service that owns the Android lifecycle for live translation.
 *
 * This class is intentionally thin — it only handles:
 *  1. Foreground service + notification (Android requirement)
 *  2. Creating and wiring the [LiveTranslationOrchestrator] with concrete implementations
 *  3. Responding to start/stop intents from [ScreenCaptureModule]
 *
 * All translation logic lives in [LiveTranslationOrchestrator] and its modules.
 * To change behaviour, swap a module implementation — do not add logic here.
 */
class ScreenCaptureService : Service() {

    companion object {
        const val TAG = "UchiaService"
        const val CHANNEL_ID = "uchia_capture"
        const val NOTIF_ID = 3001

        const val ACTION_START = "com.uchia.capture.START"
        const val ACTION_STOP  = "com.uchia.capture.STOP"

        const val EXTRA_RESULT_CODE  = "resultCode"
        const val EXTRA_RESULT_DATA  = "resultData"
        const val EXTRA_TARGET_LANG  = "targetLang"
        const val EXTRA_SOURCE_LANG  = "sourceLang"
        const val EXTRA_ENABLE_AUDIO = "enableAudio"
        const val EXTRA_API_KEY      = "apiKey"

        /** Map a BCP-47 source-language code to the MLKit OCR script needed to read it. */
        fun langToOcrScript(lang: String): MLKitOcrEngine.Script = when {
            lang.startsWith("zh") || lang == "yue"       -> MLKitOcrEngine.Script.CHINESE
            lang.startsWith("ja")                        -> MLKitOcrEngine.Script.JAPANESE
            lang.startsWith("ko")                        -> MLKitOcrEngine.Script.KOREAN
            lang in setOf("hi","mr","ne","sa","mai","kok","bho","awa","mag") ->
                MLKitOcrEngine.Script.DEVANAGARI
            else                                         -> MLKitOcrEngine.Script.LATIN
        }

        /** [ScreenCaptureModule] registers here to forward events to JS. */
        var subtitleCallback: ((original: String, translated: String) -> Unit)? = null

        private var instance: ScreenCaptureService? = null

        fun setAudioEnabled(enabled: Boolean) {
            // TODO: wire dynamic audio toggle through Orchestrator in a future pass
            Log.d(TAG, "setAudioEnabled($enabled) — restart session to apply")
        }
    }

    private var orchestrator: LiveTranslationOrchestrator? = null

    // ─── Service lifecycle ────────────────────────────────────────────────────

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSession()
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_START -> handleStart(intent)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        instance = null
        stopSession()
        super.onDestroy()
    }

    // ─── Session setup ────────────────────────────────────────────────────────

    private fun handleStart(intent: Intent) {
        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
        val resultData: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RESULT_DATA)
        }
        val targetLang   = intent.getStringExtra(EXTRA_TARGET_LANG)   ?: "en"
        val sourceLang   = intent.getStringExtra(EXTRA_SOURCE_LANG)   ?: "auto"
        val enableAudio  = intent.getBooleanExtra(EXTRA_ENABLE_AUDIO, false)
        val apiKey       = intent.getStringExtra(EXTRA_API_KEY)        ?: ""

        if (resultCode != Activity.RESULT_OK || resultData == null) {
            Log.e(TAG, "Invalid MediaProjection result — stopping")
            stopSelf()
            return
        }

        if (!Settings.canDrawOverlays(this)) {
            Log.e(TAG, "SYSTEM_ALERT_WINDOW not granted — stopping")
            stopSelf()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(NOTIF_ID, buildNotification())
        }

        val captureConfig = CaptureConfig(resultCode, resultData)
        val sessionConfig = SessionConfig(
            targetLang  = targetLang,
            sourceLang  = sourceLang,
            enableVideo = true,
            enableAudio = enableAudio,
        )

        val newOrchestrator = buildOrchestrator(apiKey, sourceLang, targetLang)
        newOrchestrator.onSubtitleEvent = { original, translated ->
            subtitleCallback?.invoke(original, translated)
        }

        orchestrator = newOrchestrator
        newOrchestrator.start(sessionConfig, captureConfig)

        Log.d(TAG, "Session started — source=$sourceLang target=$targetLang audio=$enableAudio")
    }

    private fun stopSession() {
        orchestrator?.release()
        orchestrator = null
    }

    // ─── Wiring ───────────────────────────────────────────────────────────────

    /**
     * Construct the orchestrator with concrete Android implementations.
     * This is the only place in the app that names concrete classes —
     * everything downstream depends on interfaces.
     */
    private fun buildOrchestrator(apiKey: String, sourceLang: String = "auto", targetLang: String = "zh"): LiveTranslationOrchestrator {
        val capture    = MediaProjectionCaptureEngine(this)
        val ocr        = MLKitOcrEngine(langToOcrScript(sourceLang))
        val asr        = SherpaAsrAdapter(this)
        val sampler    = FrameDiffSampler()
        val inpainter  = TemporalBackgroundInpainter()
        // Use on-device ML Kit translator when available (fast, free, offline).
        // Fall back to cloud streaming only if an API key is explicitly provided.
        val onDevice = apiKey.isBlank()
        val translator: ITranslator = if (onDevice) {
            Log.e(TAG, "No API key — using on-device ML Kit translator (0ms cooldown)")
            MLKitTranslator(targetLang = targetLang)
        } else {
            Log.e(TAG, "API key provided — using cloud streaming translator (1500ms cooldown)")
            CloudStreamingTranslator(apiKey)
        }
        val renderer   = PositionalOverlayRenderer(this)

        return LiveTranslationOrchestrator(
            capture               = capture,
            ocr                   = ocr,
            asr                   = asr,
            sampler               = sampler,
            inpainter             = inpainter,
            translator            = translator,
            renderer              = renderer,
            translationCooldownMs = if (onDevice) 0L else 1_500L,
        )
    }

    // ─── Notification ─────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Live Translation", NotificationManager.IMPORTANCE_LOW).apply {
                        description = "Uchia real-time subtitle translation"
                    }
                )
            }
        }
    }

    private fun buildNotification(): Notification {
        val stopPi = PendingIntent.getService(
            this, 0,
            Intent(this, ScreenCaptureService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Uchia Live Translation")
            .setContentText("Translating in real time — tap to stop")
            .addAction(Notification.Action.Builder(null, "Stop", stopPi).build())
            .setOngoing(true)
            .build()
    }
}
