package com.uchia.capture

import android.graphics.Bitmap

/**
 * Abstracts the screen + audio capture source.
 *
 * Android implementation: [MediaProjectionCaptureEngine] (MediaProjection API)
 * iOS implementation (future): ScreenCaptureKitEngine
 *
 * Frames are delivered at whatever rate the underlying source produces them.
 * [IFrameSampler] decides which frames actually go to OCR.
 */
interface ICaptureEngine {
    fun start(config: CaptureConfig)
    fun stop()

    /** Called on every captured frame. [timestampMs] is System.currentTimeMillis(). */
    fun setFrameCallback(cb: (frame: Bitmap, timestampMs: Long) -> Unit)

    /** Called with raw PCM audio chunks from the device / app audio. */
    fun setAudioCallback(cb: (chunk: PCMChunk) -> Unit)

    fun release()
}
