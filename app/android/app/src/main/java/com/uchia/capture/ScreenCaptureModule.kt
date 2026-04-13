package com.uchia.capture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * React Native bridge for live translation.
 *
 * JS API surface (intentionally minimal):
 *   requestPermission()                    → Promise<boolean>
 *   checkOverlayPermission()               → Promise<boolean>
 *   requestOverlayPermission()             → void
 *   start(targetLang, enableAudio, apiKey) → Promise<void>
 *   stop()                                 → Promise<void>
 *   isRunning()                            → Promise<boolean>
 *   areAsrModelsReady()                    → Promise<boolean>
 *   downloadAsrModels()                    → Promise<void>  (emits onAsrModelDownloadProgress)
 *
 * Events emitted:
 *   "onLiveSubtitle"           → { original: string, translated: string }
 *   "onAsrModelDownloadProgress" → { downloaded: number, total: number }
 */
class ScreenCaptureModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext),
    ActivityEventListener {

    companion object {
        const val NAME = "ScreenCaptureModule"
        const val REQUEST_CODE = 4001
    }

    private var pendingPromise: Promise? = null
    private var resultCode: Int = Activity.RESULT_CANCELED
    private var resultData: Intent? = null
    private var isCapturing = false

    private val sherpaModelManager by lazy { SherpaModelManager(reactContext) }

    init {
        reactContext.addActivityEventListener(this)
        ScreenCaptureService.subtitleCallback = { original, translated ->
            val params = Arguments.createMap().apply {
                putString("original", original)
                putString("translated", translated)
            }
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit("onLiveSubtitle", params)
        }
    }

    override fun getName() = NAME

    // ─── Permission ───────────────────────────────────────────────────────────

    @ReactMethod
    fun requestPermission(promise: Promise) {
        val activity = reactApplicationContext.currentActivity
        if (activity == null) {
            promise.reject("NO_ACTIVITY", "No foreground activity")
            return
        }
        pendingPromise = promise
        val pm = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        activity.startActivityForResult(pm.createScreenCaptureIntent(), REQUEST_CODE)
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE) return
        this.resultCode = resultCode
        this.resultData = data
        pendingPromise?.resolve(resultCode == Activity.RESULT_OK && data != null)
        pendingPromise = null
    }

    override fun onNewIntent(intent: Intent) {}

    @ReactMethod
    fun checkOverlayPermission(promise: Promise) {
        promise.resolve(Settings.canDrawOverlays(reactContext))
    }

    @ReactMethod
    fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${reactContext.packageName}")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        reactContext.startActivity(intent)
    }

    // ─── Start / Stop ─────────────────────────────────────────────────────────

    /**
     * @param targetLang  BCP-47 target language code (e.g. "zh", "de")
     * @param sourceLang  BCP-47 code of the video subtitle language — drives OCR script selection
     *                    (e.g. "zh" → Chinese recognizer, "ja" → Japanese, "auto" → Latin)
     * @param enableAudio true to also run the ASR → translate audio path
     * @param apiKey      Anthropic API key for [CloudStreamingTranslator]
     */
    @ReactMethod
    fun start(targetLang: String, sourceLang: String, enableAudio: Boolean, apiKey: String, promise: Promise) {
        if (resultCode != Activity.RESULT_OK || resultData == null) {
            promise.reject("NO_PERMISSION", "Call requestPermission() first")
            return
        }
        if (!Settings.canDrawOverlays(reactContext)) {
            promise.reject("NO_OVERLAY_PERMISSION", "Call requestOverlayPermission() first")
            return
        }

        val intent = Intent(reactContext, ScreenCaptureService::class.java).apply {
            action = ScreenCaptureService.ACTION_START
            putExtra(ScreenCaptureService.EXTRA_RESULT_CODE,  resultCode)
            putExtra(ScreenCaptureService.EXTRA_RESULT_DATA,  resultData)
            putExtra(ScreenCaptureService.EXTRA_TARGET_LANG,  targetLang)
            putExtra(ScreenCaptureService.EXTRA_SOURCE_LANG,  sourceLang)
            putExtra(ScreenCaptureService.EXTRA_ENABLE_AUDIO, enableAudio)
            putExtra(ScreenCaptureService.EXTRA_API_KEY,      apiKey)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reactContext.startForegroundService(intent)
        } else {
            reactContext.startService(intent)
        }

        isCapturing = true
        promise.resolve(null)
    }

    @ReactMethod
    fun stop(promise: Promise) {
        reactContext.startService(
            Intent(reactContext, ScreenCaptureService::class.java).apply {
                action = ScreenCaptureService.ACTION_STOP
            }
        )
        isCapturing = false
        promise.resolve(null)
    }

    @ReactMethod
    fun isRunning(promise: Promise) {
        promise.resolve(isCapturing)
    }

    // ─── ASR model management ─────────────────────────────────────────────────

    @ReactMethod
    fun areAsrModelsReady(promise: Promise) {
        promise.resolve(sherpaModelManager.areModelsReady())
    }

    @ReactMethod
    fun downloadAsrModels(promise: Promise) {
        sherpaModelManager.downloadModels(
            onProgress = { downloaded, total ->
                val params = Arguments.createMap().apply {
                    putInt("downloaded", downloaded)
                    putInt("total", total)
                }
                reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                    .emit("onAsrModelDownloadProgress", params)
            },
            onComplete = { success ->
                if (success) promise.resolve(null)
                else promise.reject("DOWNLOAD_FAILED", "One or more ASR model files failed")
            }
        )
    }

    // Required by RN event emitter contract
    @ReactMethod fun addListener(eventName: String) {}
    @ReactMethod fun removeListeners(count: Int) {}
}
