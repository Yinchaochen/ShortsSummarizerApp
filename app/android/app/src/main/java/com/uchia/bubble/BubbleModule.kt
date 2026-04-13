package com.uchia.bubble

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.provider.Settings
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class BubbleModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val CHANNEL_ID   = "uchia_bubbles"
        const val CHANNEL_NAME = "Uchia Bubbles"
        const val NOTIF_TRANSLATION = 1001
        const val NOTIF_PET_BASE    = 2000
    }

    override fun getName() = "BubbleModule"

    // ─── Connection test ──────────────────────────────────────────────────────

    @ReactMethod
    fun ping(promise: Promise) {
        promise.resolve("pong")
    }

    // ─── Support check ────────────────────────────────────────────────────────

    @ReactMethod
    fun isSupported(promise: Promise) {
        promise.resolve(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
    }

    // ─── Translation bubble ───────────────────────────────────────────────────

    @SuppressLint("NewApi")
    @ReactMethod
    fun showTranslationBubble(text: String, promise: Promise) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            promise.reject("UNSUPPORTED", "Bubbles require Android 11+")
            return
        }
        try {
            ensureChannel()
            val intent = Intent(reactContext, BubbleActivity::class.java).apply {
                putExtra(BubbleActivity.EXTRA_TYPE, BubbleActivity.TYPE_TRANSLATION)
                putExtra(BubbleActivity.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pi = PendingIntent.getActivity(
                reactContext, NOTIF_TRANSLATION, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
            val bubbleMeta = Notification.BubbleMetadata
                .Builder(pi, makeIcon("#7170ff", "U"))
                .setDesiredHeight(240)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build()

            val person = Person.Builder().setName("Uchia Translation").build()
            val notif = Notification.Builder(reactContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Uchia")
                .setContentText(text)
                .setBubbleMetadata(bubbleMeta)
                .addPerson(person)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .build()

            val nm = reactContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIF_TRANSLATION, notif)
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message)
        }
    }

    // ─── Pet bubble ───────────────────────────────────────────────────────────

    @SuppressLint("NewApi")
    @ReactMethod
    fun showPetBubble(petId: String, petName: String, promise: Promise) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            promise.reject("UNSUPPORTED", "Bubbles require Android 11+")
            return
        }
        try {
            ensureChannel()
            val notifId = NOTIF_PET_BASE + Math.abs(petId.hashCode() % 1000)
            val intent = Intent(reactContext, BubbleActivity::class.java).apply {
                putExtra(BubbleActivity.EXTRA_TYPE, BubbleActivity.TYPE_PET)
                putExtra(BubbleActivity.EXTRA_TEXT, petName)
                putExtra(BubbleActivity.EXTRA_PET_ID, petId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pi = PendingIntent.getActivity(
                reactContext, notifId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
            val initial = petName.firstOrNull()?.uppercaseChar()?.toString() ?: "P"
            val bubbleMeta = Notification.BubbleMetadata
                .Builder(pi, makeIcon("#4caf50", initial))
                .setDesiredHeight(200)
                .setAutoExpandBubble(false)
                .setSuppressNotification(true)
                .build()

            val person = Person.Builder().setName(petName).build()
            val notif = Notification.Builder(reactContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_myplaces)
                .setContentTitle(petName)
                .setContentText("Tap to interact")
                .setBubbleMetadata(bubbleMeta)
                .addPerson(person)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .build()

            val nm = reactContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notifId, notif)
            promise.resolve(notifId)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message)
        }
    }

    // ─── Dismiss ──────────────────────────────────────────────────────────────

    @ReactMethod
    fun dismissBubble(notifId: Int, promise: Promise) {
        val nm = reactContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notifId)
        promise.resolve(null)
    }

    // ─── Permissions ──────────────────────────────────────────────────────────

    @ReactMethod
    fun checkPermissions(promise: Promise) {
        val result = Arguments.createMap().apply {
            // AccessibilityService is no longer part of the architecture.
            // Keeping the key for backwards compat with JS callers; always false.
            putBoolean("hasAccessibilityPermission", false)
            putBoolean("supported", Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        }
        promise.resolve(result)
    }

    @ReactMethod
    fun requestAccessibilityPermission() {
        // No-op: AccessibilityService removed from architecture.
        // Kept so existing JS call sites don't crash.
    }

    // ─── Service control stubs ────────────────────────────────────────────────

    @ReactMethod
    fun start(config: ReadableMap, promise: Promise) {
        promise.reject("NOT_IMPLEMENTED", "Use ScreenCaptureModule.start() instead")
    }

    @ReactMethod
    fun stop(promise: Promise) {
        promise.reject("NOT_IMPLEMENTED", "Use ScreenCaptureModule.stop() instead")
    }

    @ReactMethod
    fun updateConfig(config: ReadableMap, promise: Promise) {
        promise.reject("NOT_IMPLEMENTED", "Phase 2 not yet implemented")
    }

    // ─── Legacy overlay config stubs ─────────────────────────────────────────
    // These were written to SharedPreferences read by SubtitleAccessibilityService.
    // Kept as no-ops so JS callers don't crash; config is now passed to
    // ScreenCaptureModule.start() directly.

    @ReactMethod
    fun setTargetLanguage(langCode: String, promise: Promise) {
        promise.resolve(null)
    }

    @ReactMethod
    fun setOverlayEnabled(enabled: Boolean, promise: Promise) {
        promise.resolve(null)
    }

    // Required by RN event emitter contract
    @ReactMethod fun addListener(eventName: String) {}
    @ReactMethod fun removeListeners(count: Int) {}

    // ─── Helpers ──────────────────────────────────────────────────────────────

    @SuppressLint("NewApi")
    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = reactContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val ch = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Uchia floating bubbles"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) setAllowBubbles(true)
                }
                nm.createNotificationChannel(ch)
            }
        }
    }

    private fun makeIcon(colorHex: String, letter: String): android.graphics.drawable.Icon {
        val size = 96
        val bmp  = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint  = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.parseColor(colorHex)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        paint.color    = Color.WHITE
        paint.textSize = 48f
        paint.textAlign = Paint.Align.CENTER
        val fm       = paint.fontMetrics
        val baseline = size / 2f - (fm.ascent + fm.descent) / 2f
        canvas.drawText(letter, size / 2f, baseline, paint)

        return android.graphics.drawable.Icon.createWithBitmap(bmp)
    }
}
