package com.uchia.capture

/**
 * Abstracts the translation service.
 *
 * Supports streaming: [onPartial] fires as tokens arrive so the UI can
 * update incrementally without waiting for the full response.
 *
 * Implementation: [CloudStreamingTranslator] (Claude / Gemini streaming API)
 *
 * To swap engines (e.g. add offline NLLB fallback), create a new implementation
 * of this interface and inject it into [LiveTranslationOrchestrator].
 */
interface ITranslator {
    /**
     * Translate [text] from [sourceLang] to [targetLang].
     *
     * [onPartial]  — called with each incremental token as it arrives (may be called 0+ times)
     * [onComplete] — called once with the final, complete translation
     * [onError]    — called if the request fails; [onComplete] will NOT be called in that case
     *
     * Thread-safe. May be called from any coroutine context.
     */
    fun translate(
        text: String,
        sourceLang: String,
        targetLang: String,
        onPartial: (String) -> Unit,
        onComplete: (String) -> Unit,
        onError: (Throwable) -> Unit,
    )

    /** Cancel any in-flight request. */
    fun cancel()

    fun release()
}
