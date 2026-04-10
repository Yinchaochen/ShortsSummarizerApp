package com.uchia.capture

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translator
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Foreground service that captures the screen via MediaProjection, runs full-screen
 * ML Kit OCR on each frame, translates every detected text block individually, then
 * renders the translations as positional bubbles at the exact coordinates of the
 * original text — all touch events pass through so the user's experience is unaffected.
 *
 * Lifecycle:
 *   1. User grants screen capture permission (MediaProjection consent dialog)
 *   2. React Native passes the result Intent to this service
 *   3. Service starts capturing at ~1 frame/sec
 *   4. Full-screen OCR → per-block translation → PositionalOverlayView update
 *   5. User stops via notification action or React Native bridge
 */
class ScreenCaptureService : Service() {

    companion object {
        const val TAG = "UchiaCapture"
        const val CHANNEL_ID = "uchia_capture"
        const val NOTIF_ID = 3001

        const val ACTION_START = "com.uchia.capture.START"
        const val ACTION_STOP = "com.uchia.capture.STOP"
        const val EXTRA_RESULT_CODE = "resultCode"
        const val EXTRA_RESULT_DATA = "resultData"
        const val EXTRA_TARGET_LANG = "targetLang"
        const val EXTRA_OCR_SCRIPT = "ocrScript"

        /** Milliseconds between frame captures. 800ms balances responsiveness vs battery. */
        const val CAPTURE_INTERVAL_MS = 800L

        /**
         * Down-scale factor for screen captures.
         * 0.5 = half resolution — ML Kit still reads text reliably, uses less memory.
         * Overlay coordinates are multiplied by 1/SCALE_FACTOR to map back to real pixels.
         */
        const val SCALE_FACTOR = 0.5f

        /** ScreenCaptureModule registers here to receive subtitle events for the JS layer. */
        var subtitleCallback: ((original: String, translated: String) -> Unit)? = null
    }

    // ─── State ────────────────────────────────────────────────────────────────

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    /** Positional bubble overlay — rendered above all apps, touch-transparent. */
    private lateinit var overlay: PositionalOverlayView

    private val ocrProcessor = OcrProcessor()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var targetLang = "en"
    private var ocrScript = "latin"
    private var lastOcrText = ""

    /** Cached ML Kit translator. Re-created when the source language changes. */
    private var translator: Translator? = null
    private var activeLangPair = ""

    // ─── Service lifecycle ────────────────────────────────────────────────────

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        overlay = PositionalOverlayView(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopCapture()
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_START -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
                val resultData: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_RESULT_DATA)
                }
                targetLang = intent.getStringExtra(EXTRA_TARGET_LANG) ?: "en"
                ocrScript = intent.getStringExtra(EXTRA_OCR_SCRIPT) ?: "latin"

                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    startForeground(NOTIF_ID, buildNotification())
                    overlay.show()
                    startCapture(resultCode, resultData)
                } else {
                    Log.e(TAG, "Invalid MediaProjection result")
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopCapture()
        scope.cancel()
        translator?.close()
        ocrProcessor.close()
        super.onDestroy()
    }

    // ─── Screen capture ───────────────────────────────────────────────────────

    private fun startCapture(resultCode: Int, data: Intent) {
        val pm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = pm.getMediaProjection(resultCode, data)

        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)

        val width = (metrics.widthPixels * SCALE_FACTOR).toInt()
        val height = (metrics.heightPixels * SCALE_FACTOR).toInt()
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "UchiaCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null, null
        )

        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection stopped by system")
                stopCapture()
                stopSelf()
            }
        }, Handler(Looper.getMainLooper()))

        scope.launch { captureLoop(width, height) }
        Log.d(TAG, "Capture started: ${width}x${height}")
    }

    private suspend fun captureLoop(width: Int, height: Int) {
        while (scope.isActive) {
            try {
                val bitmap = acquireFrame(width, height)
                if (bitmap != null) {
                    processFrame(bitmap)
                    bitmap.recycle()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Frame error: ${e.message}")
            }
            delay(CAPTURE_INTERVAL_MS)
        }
    }

    private fun acquireFrame(width: Int, height: Int): Bitmap? {
        val image = imageReader?.acquireLatestImage() ?: return null
        return try {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val rowPadding = rowStride - pixelStride * width

            val bmp = Bitmap.createBitmap(
                width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888
            )
            bmp.copyPixelsFromBuffer(buffer)

            if (rowPadding > 0) {
                val cropped = Bitmap.createBitmap(bmp, 0, 0, width, height)
                bmp.recycle()
                cropped
            } else {
                bmp
            }
        } finally {
            image.close()
        }
    }

    // ─── OCR + Translation pipeline ───────────────────────────────────────────

    private suspend fun processFrame(bitmap: Bitmap) {
        val ocrResult = ocrProcessor.processFrame(bitmap, ocrScript)

        // No text in this frame — clear immediately
        if (ocrResult == null) {
            if (lastOcrText.isNotEmpty()) {
                overlay.update(emptyList())
                lastOcrText = ""
            }
            return
        }

        // Same text as last frame — nothing to do
        if (ocrResult.text == lastOcrText) return
        lastOcrText = ocrResult.text

        // Scale factor to map from capture-space coordinates → real screen pixels
        val scale = 1f / SCALE_FACTOR   // = 2.0 when SCALE_FACTOR = 0.5

        // Translate all blocks in parallel, then render together to avoid flicker
        val translatedBlocks = coroutineScope {
            ocrResult.blocks.map { block ->
                async {
                    val translated = translateText(block.text)
                    PositionalOverlayView.TranslatedBlock(
                        text = translated,
                        left   = (block.bounds.left   * scale).toInt(),
                        top    = (block.bounds.top    * scale).toInt(),
                        right  = (block.bounds.right  * scale).toInt(),
                        bottom = (block.bounds.bottom * scale).toInt(),
                    )
                }
            }.awaitAll()
        }

        overlay.update(translatedBlocks)

        // Forward first block to JS layer for the live subtitle monitor card
        subtitleCallback?.invoke(
            ocrResult.blocks.firstOrNull()?.text ?: "",
            translatedBlocks.firstOrNull()?.text ?: ""
        )
    }

    /**
     * Identify the source language, then translate [text] to [targetLang].
     * Returns the original text if language detection fails or text is already
     * in the target language.
     */
    private suspend fun translateText(text: String): String = suspendCoroutine { cont ->
        LanguageIdentification.getClient()
            .identifyLanguage(text)
            .addOnSuccessListener { srcLang ->
                when {
                    // Undetectable or already correct language
                    srcLang == "und" || srcLang == targetLang -> cont.resume(text)

                    else -> {
                        val pair = "$srcLang->$targetLang"
                        val t = acquireTranslator(srcLang, targetLang, pair)
                        t.downloadModelIfNeeded()
                            .addOnSuccessListener {
                                t.translate(text)
                                    .addOnSuccessListener { cont.resume(it) }
                                    .addOnFailureListener { cont.resume(text) }
                            }
                            .addOnFailureListener { cont.resume(text) }
                    }
                }
            }
            .addOnFailureListener { cont.resume(text) }
    }

    /**
     * Return a cached [Translator] for the given language pair, or create one.
     * Thread-safe — multiple coroutines may call this concurrently.
     */
    @Synchronized
    private fun acquireTranslator(src: String, target: String, pair: String): Translator {
        if (pair == activeLangPair && translator != null) return translator!!
        translator?.close()
        activeLangPair = pair
        return Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(src)
                .setTargetLanguage(target)
                .build()
        ).also { translator = it }
    }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    private fun stopCapture() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        mediaProjection?.stop()
        mediaProjection = null
        overlay.hide()
        lastOcrText = ""
    }

    // ─── Notification ─────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(NotificationChannel(
                    CHANNEL_ID, "Live Translation",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Uchia screen capture for real-time subtitle translation"
                })
            }
        }
    }

    private fun buildNotification(): Notification {
        val stopPi = PendingIntent.getService(
            this, 0,
            Intent(this, ScreenCaptureService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Uchia Live Translation")
            .setContentText("Translating text in real time — tap to stop")
            .addAction(Notification.Action.Builder(null, "Stop", stopPi).build())
            .setOngoing(true)
            .build()
    }
}
