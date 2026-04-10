package com.uchia.capture

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView

/**
 * A full-screen, touch-transparent overlay that renders one bubble per detected text block.
 *
 * Each bubble is positioned at the exact screen coordinates returned by ML Kit OCR,
 * sized to match the original text bounds. All touch events fall through to the app
 * underneath — the user can interact normally while bubbles are visible.
 *
 * Requires android.permission.SYSTEM_ALERT_WINDOW (declared in AndroidManifest).
 */
class PositionalOverlayView(private val context: Context) {

    // ─── Types ────────────────────────────────────────────────────────────────

    data class TranslatedBlock(
        val text: String,
        /** Screen coordinates in real pixels (already scaled from capture space). */
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
    )

    // ─── State ────────────────────────────────────────────────────────────────

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private val container = FrameLayout(context)
    private var added = false

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
        // Touch-transparent: taps fall through to the underlying app
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Attach the overlay window. Must be called before [update].
     * Silently no-ops if SYSTEM_ALERT_WINDOW is not yet granted.
     */
    fun show() {
        mainHandler.post {
            if (added) return@post
            try {
                wm.addView(container, layoutParams)
                added = true
            } catch (_: Exception) {
                // SYSTEM_ALERT_WINDOW not granted — ScreenCaptureModule will
                // surface this error to React Native via the start() promise.
            }
        }
    }

    /**
     * Replace all visible bubbles with [blocks].
     * Pass an empty list to clear the overlay.
     * Thread-safe — can be called from any thread.
     */
    fun update(blocks: List<TranslatedBlock>) {
        mainHandler.post {
            container.removeAllViews()
            if (!added) return@post
            for (block in blocks) {
                container.addView(buildBubble(block))
            }
        }
    }

    /**
     * Remove all bubbles and detach the overlay window.
     * Thread-safe — can be called from any thread.
     */
    fun hide() {
        mainHandler.post {
            container.removeAllViews()
            if (added) {
                try { wm.removeView(container) } catch (_: Exception) {}
                added = false
            }
        }
    }

    // ─── Bubble construction ──────────────────────────────────────────────────

    private fun buildBubble(block: TranslatedBlock): TextView {
        val dp = context.resources.displayMetrics.density
        val padH = (8 * dp).toInt()
        val padV = (4 * dp).toInt()
        val minHeight = (22 * dp).toInt()

        // Semi-transparent dark rounded background — matches app brand (#0F0F1A)
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 6 * dp
            setColor(0xDD0F0F1A.toInt())
        }

        val tv = TextView(context).apply {
            text = block.text
            setTextColor(Color.WHITE)
            textSize = 13f
            setPadding(padH, padV, padH, padV)
            background = bg
        }

        // Size the bubble to match the OCR bounding box + padding
        val bubbleW = (block.right - block.left) + padH * 2
        val bubbleH = ((block.bottom - block.top) + padV * 2).coerceAtLeast(minHeight)

        tv.layoutParams = FrameLayout.LayoutParams(bubbleW, bubbleH).apply {
            leftMargin = block.left
            topMargin = block.top
        }

        return tv
    }
}
