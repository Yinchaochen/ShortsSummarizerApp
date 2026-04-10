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
 * React Native bridge for screen-capture-based live translation.
 *
 * JS API:
 *   ScreenCaptureModule.requestPermission()   → triggers MediaProjection consent dialog
 *   ScreenCaptureModule.start(targetLang, ocrScript) → starts capture + OCR + overlay
 *   ScreenCaptureModule.stop()                → stops capture
 *   ScreenCaptureModule.isRunning()           → returns boolean
 *
 * Events emitted:
 *   "onLiveSubtitle" → { original: string, translated: string }
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

    init {
        reactContext.addActivityEventListener(this)
        // Register subtitle callback
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

    // ─── Permission request ───────────────────────────────────────────────────

    /**
     * Shows the system "Start recording?" consent dialog.
     * Resolves with true if user approved, false if denied.
     */
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

        val granted = resultCode == Activity.RESULT_OK && data != null
        pendingPromise?.resolve(granted)
        pendingPromise = null
    }

    override fun onNewIntent(intent: Intent) {}

    // ─── Start / Stop ─────────────────────────────────────────────────────────

    /**
     * Start screen capture + live translation.
     * Must call requestPermission() first and get a true result.
     *
     * Rejects with "NO_OVERLAY_PERMISSION" if the user hasn't granted
     * SYSTEM_ALERT_WINDOW — the JS layer should then call requestOverlayPermission()
     * to guide the user to the system settings page.
     *
     * @param targetLang BCP 47 language code to translate into (e.g. "zh", "en", "de")
     * @param ocrScript  OCR recognizer script: "latin", "chinese", "japanese", "korean"
     */
    @ReactMethod
    fun start(targetLang: String, ocrScript: String, promise: Promise) {
        if (resultCode != Activity.RESULT_OK || resultData == null) {
            promise.reject("NO_PERMISSION", "Screen capture permission not granted. Call requestPermission() first.")
            return
        }

        // Overlay permission check — required for PositionalOverlayView
        if (!Settings.canDrawOverlays(reactContext)) {
            promise.reject(
                "NO_OVERLAY_PERMISSION",
                "Display over other apps permission is required. Call requestOverlayPermission() to open settings."
            )
            return
        }

        val intent = Intent(reactContext, ScreenCaptureService::class.java).apply {
            action = ScreenCaptureService.ACTION_START
            putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode)
            putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, resultData)
            putExtra(ScreenCaptureService.EXTRA_TARGET_LANG, targetLang)
            putExtra(ScreenCaptureService.EXTRA_OCR_SCRIPT, ocrScript)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reactContext.startForegroundService(intent)
        } else {
            reactContext.startService(intent)
        }

        isCapturing = true
        promise.resolve(null)
    }

    /**
     * Stop the screen capture service.
     */
    @ReactMethod
    fun stop(promise: Promise) {
        val intent = Intent(reactContext, ScreenCaptureService::class.java).apply {
            action = ScreenCaptureService.ACTION_STOP
        }
        reactContext.startService(intent)
        isCapturing = false
        promise.resolve(null)
    }

    /**
     * Check if capture is currently running.
     */
    @ReactMethod
    fun isRunning(promise: Promise) {
        promise.resolve(isCapturing)
    }

    /**
     * Check whether SYSTEM_ALERT_WINDOW ("Display over other apps") is granted.
     * Call this before start() to decide whether to show a permission prompt in JS.
     */
    @ReactMethod
    fun checkOverlayPermission(promise: Promise) {
        promise.resolve(Settings.canDrawOverlays(reactContext))
    }

    /**
     * Open the system "Display over other apps" settings page for this app.
     * Call this when checkOverlayPermission() returns false.
     */
    @ReactMethod
    fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${reactContext.packageName}")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        reactContext.startActivity(intent)
    }

    // Required by RN event emitter contract
    @ReactMethod fun addListener(eventName: String) {}
    @ReactMethod fun removeListeners(count: Int) {}
}
