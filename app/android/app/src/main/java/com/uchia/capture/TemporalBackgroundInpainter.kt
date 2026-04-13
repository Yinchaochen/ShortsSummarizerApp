package com.uchia.capture

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF

/**
 * [IInpainter] that builds a background model via exponential moving average (EMA)
 * over successive frames, then uses it to:
 *  1. Sample the dominant background color for each text region ([sampleColor])
 *  2. Return an inpainted frame with text regions replaced by background content ([inpaint])
 *
 * ## Algorithm
 * - Maintain three float arrays (R, G, B) matching the capture frame dimensions.
 * - On each [updateBackground] call, update pixels NOT inside [textRegions] with:
 *     model[i] = model[i] * (1 - alpha) + frame[i] * alpha
 * - Pixels inside text regions are skipped, so persistent subtitles never contaminate
 *   the background model.
 * - [inpaint] paints the background model's pixels over the text regions.
 *
 * ## Performance
 * - Uses [Bitmap.getPixels] / [Bitmap.setPixels] (single JNI round-trip per call).
 * - All heavy work is on the caller's coroutine — no internal threading.
 * - Memory: 3 × W × H floats ≈ 3 × 0.5MP × 4 B ≈ 6 MB at half-resolution capture.
 *
 * ## Limitations
 * - Assumes a slowly-changing background (e.g., video scenes, mostly static shots).
 * - Fast camera motion causes temporal smearing; alpha controls the trade-off.
 */
class TemporalBackgroundInpainter(
    /** EMA update weight per frame. Lower = more stable model, slower convergence. */
    private val alpha: Float = 0.08f,
) : IInpainter {

    private var bgR: FloatArray? = null
    private var bgG: FloatArray? = null
    private var bgB: FloatArray? = null
    private var modelW = 0
    private var modelH = 0

    private val lock = Any()

    // ─── IInpainter ───────────────────────────────────────────────────────────

    override fun updateBackground(frame: Bitmap, textRegions: List<RectF>) {
        val w = frame.width
        val h = frame.height
        val pixels = IntArray(w * h)
        frame.getPixels(pixels, 0, w, 0, 0, w, h)

        synchronized(lock) {
            if (bgR == null || modelW != w || modelH != h) {
                modelW = w; modelH = h
                bgR = FloatArray(w * h)
                bgG = FloatArray(w * h)
                bgB = FloatArray(w * h)
                // Cold start: initialise directly from first frame
                for (i in pixels.indices) {
                    val px = pixels[i]
                    bgR!![i] = Color.red(px).toFloat()
                    bgG!![i] = Color.green(px).toFloat()
                    bgB!![i] = Color.blue(px).toFloat()
                }
                return
            }

            val inv = 1f - alpha
            for (y in 0 until h) {
                for (x in 0 until w) {
                    // Skip pixels that are currently covered by detected text
                    if (textRegions.isNotEmpty() && isInsideAny(x.toFloat(), y.toFloat(), textRegions)) continue

                    val i  = y * w + x
                    val px = pixels[i]
                    bgR!![i] = bgR!![i] * inv + Color.red(px)   * alpha
                    bgG!![i] = bgG!![i] * inv + Color.green(px) * alpha
                    bgB!![i] = bgB!![i] * inv + Color.blue(px)  * alpha
                }
            }
        }
    }

    /**
     * Replace each region in [frame] with pixels from the background model.
     * Returns a new mutable Bitmap (caller owns it and must recycle when done).
     * If the model is not yet initialised, returns [frame] unchanged.
     */
    override fun inpaint(frame: Bitmap, regions: List<RectF>): Bitmap {
        if (regions.isEmpty()) return frame

        val (r, g, b, w, h) = synchronized(lock) {
            val r = bgR ?: return frame
            val g = bgG ?: return frame
            val b = bgB ?: return frame
            Array5(r.copyOf(), g.copyOf(), b.copyOf(), modelW, modelH)
        }
        if (w != frame.width || h != frame.height) return frame

        val pixels = IntArray(w * h)
        frame.getPixels(pixels, 0, w, 0, 0, w, h)

        for (region in regions) {
            val x0 = region.left.toInt().coerceIn(0, w - 1)
            val y0 = region.top.toInt().coerceIn(0, h - 1)
            val x1 = region.right.toInt().coerceIn(0, w)
            val y1 = region.bottom.toInt().coerceIn(0, h)
            for (py in y0 until y1) {
                for (px in x0 until x1) {
                    val i = py * w + px
                    pixels[i] = Color.rgb(r[i].toInt(), g[i].toInt(), b[i].toInt())
                }
            }
        }

        val result = frame.copy(Bitmap.Config.ARGB_8888, true)
        result.setPixels(pixels, 0, w, 0, 0, w, h)
        return result
    }

    /**
     * Sample the average background color inside [region] (capture-space coords).
     * Returns [Color.TRANSPARENT] if the model is not yet initialised.
     */
    fun sampleColor(region: RectF): Int {
        val (r, g, b, w, h) = synchronized(lock) {
            val r = bgR ?: return Color.TRANSPARENT
            val g = bgG ?: return Color.TRANSPARENT
            val b = bgB ?: return Color.TRANSPARENT
            Array5(r, g, b, modelW, modelH)
        }

        val x0 = region.left.toInt().coerceIn(0, w - 1)
        val y0 = region.top.toInt().coerceIn(0, h - 1)
        val x1 = region.right.toInt().coerceIn(0, w)
        val y1 = region.bottom.toInt().coerceIn(0, h)

        var sumR = 0f; var sumG = 0f; var sumB = 0f; var count = 0
        for (py in y0 until y1) {
            for (px in x0 until x1) {
                val i = py * w + px
                sumR += r[i]; sumG += g[i]; sumB += b[i]; count++
            }
        }
        if (count == 0) return Color.TRANSPARENT
        return Color.rgb((sumR / count).toInt(), (sumG / count).toInt(), (sumB / count).toInt())
    }

    override fun release() {
        synchronized(lock) {
            bgR = null; bgG = null; bgB = null
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun isInsideAny(x: Float, y: Float, regions: List<RectF>): Boolean {
        for (r in regions) if (r.contains(x, y)) return true
        return false
    }

    /** Tiny value class to avoid Pair nesting for 5 values. */
    private data class Array5(
        val r: FloatArray, val g: FloatArray, val b: FloatArray,
        val w: Int, val h: Int,
    )
}
