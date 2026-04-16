package com.uchia.capture

import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics

/**
 * Timer-based caption synchronizer for platform-fetched captions.
 *
 * Load pre-translated [CaptionSegment] objects with [load], then call [start]
 * when the video begins playing (t = 0). A 100 ms ticker finds all segments
 * active at [currentTimeSec] and forwards them to the [ISubtitleRenderer].
 *
 * Speech captions (x == null) → [ISubtitleRenderer.updateAudioBar]
 * Text stickers  (x != null) → [ISubtitleRenderer.update] at relative screen position
 *
 * Thread-safe: [start] and [stop] may be called from any thread.
 */
class CaptionSyncManager(
    private val renderer: ISubtitleRenderer,
    private val displayMetrics: DisplayMetrics,
) {

    data class CaptionSegment(
        val startSec: Float,
        val endSec: Float,
        /** Translated text to display. */
        val text: String,
        /** Relative horizontal centre (0.0–1.0). null = speech caption. */
        val x: Float?,
        /** Relative vertical centre (0.0–1.0). null = speech caption. */
        val y: Float?,
    )

    private val handler = Handler(Looper.getMainLooper())
    private var segments: List<CaptionSegment> = emptyList()
    private var startMs: Long = 0L
    @Volatile private var running = false

    /** Elapsed playback time in seconds since [start] was called. */
    val currentTimeSec: Float
        get() = if (running) (System.currentTimeMillis() - startMs) / 1000f else 0f

    // ─── Public API ───────────────────────────────────────────────────────────

    /** Replace the loaded segment list. Safe to call while running. */
    fun load(newSegments: List<CaptionSegment>) {
        segments = newSegments.sortedBy { it.startSec }
    }

    /**
     * Attach the overlay and start ticking from t = 0.
     * If already running, restarts from t = 0.
     */
    fun start() {
        handler.post {
            running = false
            handler.removeCallbacks(tickRunnable)
            renderer.show()
            startMs = System.currentTimeMillis()
            running = true
            handler.post(tickRunnable)
        }
    }

    /** Stop the ticker, clear the overlay, and detach the overlay window. */
    fun stop() {
        handler.post {
            running = false
            handler.removeCallbacks(tickRunnable)
            renderer.updateAudioBar(null)
            renderer.update(emptyList())
            renderer.hide()
        }
    }

    // ─── Ticker ───────────────────────────────────────────────────────────────

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            val nowSec = (System.currentTimeMillis() - startMs) / 1000f
            val active = segments.filter { it.startSec <= nowSec && nowSec < it.endSec }

            // Speech captions → subtitle bar (latest one wins on overlap)
            val speech = active.lastOrNull { it.x == null }
            renderer.updateAudioBar(speech?.text)

            // Text stickers → positional bubbles
            val stickers = active.filter { it.x != null }
            renderer.update(stickers.map { it.toRenderedSubtitle() })

            handler.postDelayed(this, 100L)
        }
    }

    // ─── Coordinate mapping ───────────────────────────────────────────────────

    /**
     * Convert relative (0–1) sticker centre coords to a [RenderedSubtitle] with
     * a screen-pixel bounding box.  Default sticker size: 220 dp wide × 36 dp tall.
     */
    private fun CaptionSegment.toRenderedSubtitle(): RenderedSubtitle {
        val sw    = displayMetrics.widthPixels.toFloat()
        val sh    = displayMetrics.heightPixels.toFloat()
        val dp    = displayMetrics.density
        val cx    = (x ?: 0.5f) * sw
        val cy    = (y ?: 0.5f) * sh
        val halfW = 110f * dp
        val halfH = 18f  * dp
        return RenderedSubtitle(
            text        = text,
            boundingBox = RectF(cx - halfW, cy - halfH, cx + halfW, cy + halfH),
        )
    }
}
