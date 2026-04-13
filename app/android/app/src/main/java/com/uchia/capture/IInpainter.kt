package com.uchia.capture

import android.graphics.Bitmap
import android.graphics.RectF

/**
 * Abstracts the background reconstruction (inpainting) engine.
 *
 * Erases detected text regions from a frame and fills them with
 * estimated background content so translated text can be rendered in place.
 *
 * Android v1: [OpenCVInpainter]   — Telea algorithm, fast, good for static BG
 * Android v2: [NcnnInpainter]     — deep model via NCNN, handles dynamic BG
 * iOS (future): CoreMLInpainter
 *
 * Phase 1 ships without inpainting (overlay-only). Swap in an implementation
 * here when ready without touching any other module.
 */
interface IInpainter {
    /**
     * Erase [regions] from [frame] and return the inpainted result.
     * The returned bitmap may be the same object as [frame] (in-place) or a new one.
     */
    fun inpaint(frame: Bitmap, regions: List<RectF>): Bitmap

    /**
     * Feed [frame] to the background model.
     * [textRegions] — regions currently occupied by text in capture-space pixels.
     * These pixels are excluded from the background update so the model stays clean
     * even when subtitles are persistent across many frames.
     * Call this on EVERY frame, before [inpaint].
     */
    fun updateBackground(frame: Bitmap, textRegions: List<RectF> = emptyList())

    fun release()
}
