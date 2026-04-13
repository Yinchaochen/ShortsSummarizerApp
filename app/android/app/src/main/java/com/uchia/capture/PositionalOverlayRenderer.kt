package com.uchia.capture

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView

/**
 * [ISubtitleRenderer] backed by a full-screen, touch-transparent WindowManager overlay.
 *
 * Renders one bubble per [RenderedSubtitle], positioned at the exact screen
 * coordinates returned by OCR. A separate "audio bar" at the bottom shows
 * live ASR output independently from the visual subtitle bubbles.
 *
 * All public methods are thread-safe — they marshal to the main thread internally.
 */
class PositionalOverlayRenderer(
    private val context: Context,
    private val windowType: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
) : ISubtitleRenderer {

    private val wm          = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private val container   = FrameLayout(context)
    private var added       = false

    private val statusBarHeight: Int by lazy {
        val id = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (id > 0) context.resources.getDimensionPixelSize(id) else 0
    }

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        windowType,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT,
    ).apply { gravity = Gravity.TOP or Gravity.START }

    // ─── ISubtitleRenderer ────────────────────────────────────────────────────

    override fun show() {
        mainHandler.post {
            if (added) return@post
            try {
                wm.addView(container, layoutParams)
                added = true
            } catch (_: Exception) {}
        }
    }

    override fun update(subtitles: List<RenderedSubtitle>) {
        mainHandler.post {
            if (!added) return@post
            val audioBar = container.findViewWithTag<View>(TAG_AUDIO_BAR)
            container.removeAllViews()
            audioBar?.let { container.addView(it) }
            for (subtitle in subtitles) {
                // Phase 2: paint a cover patch first if we have background color
                if (subtitle.coverColor != Color.TRANSPARENT) {
                    container.addView(buildCoverPatch(subtitle))
                }
                container.addView(buildBubble(subtitle))
            }
        }
    }

    override fun updateAudioBar(text: String?) {
        mainHandler.post {
            if (!added) return@post
            container.findViewWithTag<android.view.View>(TAG_AUDIO_BAR)?.let {
                container.removeView(it)
            }
            val clean = text?.trim()
            if (!clean.isNullOrEmpty()) container.addView(buildAudioBar(clean))
        }
    }

    override fun hide() {
        mainHandler.post {
            container.removeAllViews()
            if (added) {
                try { wm.removeView(container) } catch (_: Exception) {}
                added = false
            }
        }
    }

    override fun release() {
        hide()
    }

    // ─── View builders ────────────────────────────────────────────────────────

    /**
     * Opaque rectangle that covers the original subtitle text.
     * Color is sampled from the temporal background model so it blends
     * naturally with the surrounding video content.
     */
    private fun buildCoverPatch(subtitle: RenderedSubtitle): View {
        val box = subtitle.boundingBox
        val dp  = context.resources.displayMetrics.density

        val bg = GradientDrawable().apply {
            shape        = GradientDrawable.RECTANGLE
            cornerRadius = 4 * dp
            setColor(subtitle.coverColor)
        }

        return View(context).apply {
            background = bg
            layoutParams = FrameLayout.LayoutParams(
                box.width().toInt().coerceAtLeast(1),
                box.height().toInt().coerceAtLeast(1),
            ).apply {
                leftMargin = box.left.toInt().coerceAtLeast(0)
                topMargin  = (box.top - statusBarHeight).toInt().coerceAtLeast(0)
            }
        }
    }

    private fun buildBubble(subtitle: RenderedSubtitle): TextView {
        val dp     = context.resources.displayMetrics.density
        val padH   = (8  * dp).toInt()
        val padV   = (4  * dp).toInt()
        val minH   = (22 * dp).toInt()
        val box    = subtitle.boundingBox

        val bg = GradientDrawable().apply {
            shape        = GradientDrawable.RECTANGLE
            cornerRadius = 6 * dp
            setColor(0xDD0F0F1A.toInt())
        }

        val bubbleW = (box.width()  + padH * 2).toInt()
        val bubbleH = (box.height() + padV * 2).toInt().coerceAtLeast(minH)

        return TextView(context).apply {
            text = subtitle.text
            setTextColor(Color.WHITE)
            textSize = 13f
            setPadding(padH, padV, padH, padV)
            background = bg
            layoutParams = FrameLayout.LayoutParams(bubbleW, bubbleH).apply {
                leftMargin = (box.left - padH).toInt().coerceAtLeast(0)
                topMargin  = (box.top  - padV - statusBarHeight).toInt().coerceAtLeast(0)
            }
        }
    }

    private fun buildAudioBar(text: String): TextView {
        val dp      = context.resources.displayMetrics.density
        val metrics = context.resources.displayMetrics

        val bg = GradientDrawable().apply {
            shape        = GradientDrawable.RECTANGLE
            cornerRadius = 8 * dp
            setColor(0xEE0F0F1A.toInt())
        }

        return TextView(context).apply {
            tag = TAG_AUDIO_BAR
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding((16 * dp).toInt(), (10 * dp).toInt(), (16 * dp).toInt(), (10 * dp).toInt())
            background = bg
            layoutParams = FrameLayout.LayoutParams(
                (metrics.widthPixels * 0.92f).toInt(),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            ).apply { bottomMargin = (80 * dp).toInt() }
        }
    }

    companion object {
        private const val TAG_AUDIO_BAR = "audio_bar"
    }
}
