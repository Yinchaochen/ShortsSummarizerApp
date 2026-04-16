package com.uchia.capture

import android.graphics.Bitmap

/**
 * [IFrameSampler] using per-tile pixel-difference to decide which frames
 * are worth sending to OCR.
 *
 * The frame is divided into [tileRows] × [tileCols] tiles. Each tile is
 * sampled at [samplesPerTile] random pixels. A tile is "changed" if its
 * average absolute luminance difference exceeds [tileThreshold].
 *
 * A frame is processed if at least [minChangedTiles] tiles changed.
 *
 * This approach:
 *  - Makes no assumptions about where text appears on screen (no fixed regions)
 *  - Is O(tileRows × tileCols × samplesPerTile) — very cheap compared to OCR
 *  - Naturally skips static frames (e.g. paused video) at zero OCR cost
 */
class FrameDiffSampler(
    private val tileRows: Int = 4,
    private val tileCols: Int = 4,
    private val samplesPerTile: Int = 16,
    private val tileThreshold: Float = 12f,  // luminance units 0-255
    private val minChangedTiles: Int = 1,    // 1 tile changed is enough to trigger OCR
) : IFrameSampler {

    override fun shouldProcess(current: Bitmap, previous: Bitmap?): Boolean {
        if (previous == null) return true
        if (current.width != previous.width || current.height != previous.height) return true

        val tileW = current.width  / tileCols
        val tileH = current.height / tileRows
        if (tileW < 1 || tileH < 1) return true

        var changedTiles = 0

        outer@ for (row in 0 until tileRows) {
            for (col in 0 until tileCols) {
                val x0 = col * tileW
                val y0 = row * tileH

                var sumDiff = 0f
                repeat(samplesPerTile) {
                    val px = x0 + (Math.random() * tileW).toInt()
                    val py = y0 + (Math.random() * tileH).toInt()
                    sumDiff += luminanceDiff(current.getPixel(px, py), previous.getPixel(px, py))
                }

                if (sumDiff / samplesPerTile >= tileThreshold) {
                    changedTiles++
                    if (changedTiles >= minChangedTiles) break@outer
                }
            }
        }

        return changedTiles >= minChangedTiles
    }

    /** Fast luminance approximation: 0.299R + 0.587G + 0.114B */
    private fun luminanceDiff(a: Int, b: Int): Float {
        val dr = ((a shr 16 and 0xFF) - (b shr 16 and 0xFF)).toFloat()
        val dg = ((a shr  8 and 0xFF) - (b shr  8 and 0xFF)).toFloat()
        val db = ((a        and 0xFF) - (b        and 0xFF)).toFloat()
        return Math.abs(0.299f * dr + 0.587f * dg + 0.114f * db)
    }
}
