package com.uchia.capture

import android.graphics.Bitmap

/**
 * Abstracts the OCR text detection engine.
 *
 * Android implementation: [MLKitOcrEngine] (Google MLKit Text Recognition v2)
 * iOS implementation (future): VisionOcrEngine (Apple Vision framework)
 *
 * [detect] scans the full bitmap — no region assumptions.
 * The returned list may be empty if no text is found.
 */
interface IOcrEngine {
    /**
     * Detect all text blocks in [frame].
     * Must be safe to call from a background coroutine.
     */
    suspend fun detect(frame: Bitmap): List<TextBlock>

    fun release()
}
