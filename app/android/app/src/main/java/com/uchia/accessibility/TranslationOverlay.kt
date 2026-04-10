package com.uchia.accessibility

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Floating overlay drawn via TYPE_ACCESSIBILITY_OVERLAY.
 * No SYSTEM_ALERT_WINDOW permission needed — covered by the AccessibilityService grant.
 *
 * Show/hide must be called on the main thread.
 */
class TranslationOverlay(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private var root: LinearLayout? = null
    private var contentView: TextView? = null
    private var isShowing = false

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
        // NOT_TOUCHABLE = all taps fall through to the app underneath
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        // Lift above the navigation bar (home/back buttons sit ~72–96 dp from bottom)
        y = (96 * context.resources.displayMetrics.density).toInt()
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    /** Show or update the overlay with [translatedText]. Thread-safe. */
    fun update(translatedText: String) {
        mainHandler.post {
            ensureView()
            contentView?.text = translatedText
            if (!isShowing) {
                try {
                    windowManager.addView(root, layoutParams)
                    isShowing = true
                } catch (e: Exception) {
                    // Service not yet fully connected
                }
            }
        }
    }

    /** Hide the overlay. Thread-safe. */
    fun hide() {
        mainHandler.post {
            if (isShowing && root != null) {
                try {
                    windowManager.removeView(root)
                } catch (_: Exception) {}
                isShowing = false
            }
        }
    }

    // ─── View construction ───────────────────────────────────────────────────

    private fun ensureView() {
        if (root != null) return

        val dp = context.resources.displayMetrics.density

        root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xE00f0f1a.toInt())
            val pad = (16 * dp).toInt()
            val padBottom = (24 * dp).toInt()
            setPadding(pad, (10 * dp).toInt(), pad, padBottom)
        }

        // "UCHIA" label
        root!!.addView(TextView(context).apply {
            text = "UCHIA"
            textSize = 9f
            setTextColor(0xFF7170ff.toInt())
            letterSpacing = 0.2f
            val pb = (6 * dp).toInt()
            setPadding(0, 0, 0, pb)
        })

        // Translation content
        contentView = TextView(context).apply {
            textSize = 15f
            setTextColor(0xFFf7f8f8.toInt())
            lineHeight = (textSize * 1.6f * dp).toInt()
        }
        root!!.addView(contentView)
    }
}
