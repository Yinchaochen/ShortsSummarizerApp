package com.uchia.capture

/**
 * Abstracts the streaming speech recognition engine.
 *
 * Android implementation: [SherpaAsrEngine] (Sherpa-ONNX + SenseVoice model)
 * iOS implementation (future): same SherpaAsrEngine via ONNX, or AppleSpeechEngine
 *
 * Audio is fed incrementally via [feedAudio]; partial and final results are
 * delivered asynchronously through callbacks — do not block in these callbacks.
 */
interface IAsrEngine {
    fun start()
    fun stop()

    /** Feed a PCM chunk into the recogniser. Thread-safe. */
    fun feedAudio(chunk: PCMChunk)

    /** Flush any buffered audio and force a final result. */
    fun flush()

    /** Fired continuously as recognition progresses within an utterance. */
    fun setPartialResultCallback(cb: (text: String) -> Unit)

    /** Fired when an utterance boundary is detected and recognition is final. */
    fun setFinalResultCallback(cb: (text: String) -> Unit)

    fun isReady(): Boolean
    fun release()
}
