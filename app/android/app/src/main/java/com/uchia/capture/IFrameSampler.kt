package com.uchia.capture

import android.graphics.Bitmap

/**
 * Decides which captured frames are worth sending to the OCR engine.
 *
 * The default implementation [FrameDiffSampler] uses per-tile pixel difference
 * to skip frames where nothing meaningful has changed, reducing OCR load
 * without adding any region-position assumptions.
 *
 * Same implementation is used on Android and iOS — no platform-specific code.
 */
interface IFrameSampler {
    /**
     * Returns true if [current] differs enough from [previous] to warrant OCR.
     * [previous] is null on the very first frame (always returns true).
     */
    fun shouldProcess(current: Bitmap, previous: Bitmap?): Boolean
}
