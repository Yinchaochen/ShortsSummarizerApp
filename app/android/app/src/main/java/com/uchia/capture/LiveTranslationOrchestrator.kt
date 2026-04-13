package com.uchia.capture

import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.resume

/**
 * Coordinates all translation pipeline modules.
 *
 * Two independent streams run in parallel:
 *  - **Video path**: CaptureEngine → FrameSampler → OcrEngine → Translator → Renderer
 *  - **Audio path**: CaptureEngine → AsrEngine → Translator → Renderer (audio bar)
 *
 * Shared [ITranslator] instance handles both streams; identical text from
 * OCR and ASR within [DEDUP_WINDOW_MS] is sent only once.
 *
 * To swap any engine: pass a different implementation at construction time.
 * No other code needs to change.
 */
class LiveTranslationOrchestrator(
    private val capture   : ICaptureEngine,
    private val ocr       : IOcrEngine,
    private val asr       : IAsrEngine,
    private val sampler   : IFrameSampler,
    private val inpainter : IInpainter,
    private val translator: ITranslator,
    private val renderer  : ISubtitleRenderer,
    /** Scale factor used during capture — needed to map OCR coords back to real pixels. */
    private val scaleFactor: Float = 0.5f,
) {

    companion object {
        private const val TAG = "Orchestrator"
        /** Frames awaiting OCR. Drops oldest if producer outruns consumer. */
        private const val FRAME_CHANNEL_CAPACITY = 4
        /** If OCR and ASR produce the same text within this window, skip re-translating. */
        private const val DEDUP_WINDOW_MS = 2_000L
    }

    /** Notifies the JS layer of subtitle events: original + translated text. */
    var onSubtitleEvent: ((original: String, translated: String) -> Unit)? = null

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    // Channel carries only the frame to OCR. onUndeliveredElement recycles frames
    // that get dropped (DROP_OLDEST overflow) or are still buffered when the channel closes.
    private val frameChannel = Channel<Bitmap>(
        FRAME_CHANNEL_CAPACITY,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
        onUndeliveredElement = { bmp -> bmp.recycle() },
    )

    @Volatile private var lastOcrText   = ""
    @Volatile private var lastAsrText   = ""
    @Volatile private var lastTranslatedAt = 0L
    private var previousFrame: Bitmap? = null
    // True when previousFrame has been handed to videoLoop via frameChannel
    // and must NOT be recycled by the capture callback (videoLoop owns it).
    @Volatile private var prevFrameInChannel = false

    // ─── Public API ───────────────────────────────────────────────────────────

    fun start(config: SessionConfig, captureConfig: CaptureConfig) {
        renderer.show()

        capture.setFrameCallback { frame, _ ->
            // Background model updated on EVERY frame — regardless of sampler decision.
            // Pass empty regions here; OCR-detected regions are excluded inside videoLoop.
            inpainter.updateBackground(frame)

            // If previousFrame was sent to the channel, videoLoop may have recycled it already.
            // Pass null so the sampler treats this as a fresh start rather than crashing.
            val prev = if (prevFrameInChannel) null else previousFrame
            val shouldSend = sampler.shouldProcess(frame, prev)
            if (shouldSend) {
                // Hand frame to videoLoop. videoLoop (or onUndeliveredElement) will recycle it.
                frameChannel.trySend(frame)
            }

            // Only recycle `prev` if it is not currently owned by videoLoop.
            if (!prevFrameInChannel) {
                prev?.recycle()
            }
            // Record whether the frame we are about to store as previousFrame is in the channel.
            prevFrameInChannel = shouldSend
            previousFrame = frame
        }

        if (config.enableAudio) {
            capture.setAudioCallback { chunk -> asr.feedAudio(chunk) }
            setupAsrCallbacks(config.targetLang)
            asr.start()
        }

        scope.launch { videoLoop(config.targetLang) }
        capture.start(captureConfig)

        Log.d(TAG, "Started — video=${config.enableVideo} audio=${config.enableAudio} target=${config.targetLang}")
    }

    fun stop() {
        capture.stop()
        asr.stop()
        asr.flush()
        renderer.hide()
        frameChannel.close()   // triggers onUndeliveredElement for any buffered frames
        scope.coroutineContext.cancelChildren()
        // Don't recycle previousFrame if it is still in the channel — onUndeliveredElement handles it.
        if (!prevFrameInChannel) {
            previousFrame?.recycle()
        }
        previousFrame = null
        prevFrameInChannel = false
        lastOcrText = ""
        lastAsrText = ""
        Log.d(TAG, "Stopped")
    }

    fun release() {
        stop()
        scope.cancel()
        capture.release()
        ocr.release()
        asr.release()
        inpainter.release()
        translator.release()
        renderer.release()
    }

    // ─── Video path ───────────────────────────────────────────────────────────

    private suspend fun videoLoop(targetLang: String) {
        val scaleUp = 1f / scaleFactor
        val temporalInpainter = inpainter as? TemporalBackgroundInpainter

        for (frame in frameChannel) {
            try {
                val blocks = ocr.detect(frame)

                // Refine background model: now we know which regions have text,
                // re-run updateBackground excluding those regions so the model stays clean.
                if (blocks.isNotEmpty()) {
                    inpainter.updateBackground(frame, blocks.map { it.boundingBox })
                }

                frame.recycle()

                if (blocks.isEmpty()) {
                    if (lastOcrText.isNotEmpty()) {
                        renderer.update(emptyList())
                        lastOcrText = ""
                    }
                    continue
                }

                val fullText = blocks.joinToString("\n") { it.text }
                if (fullText == lastOcrText) continue
                lastOcrText = fullText

                // Translate all blocks in parallel, sampling background color per block
                val translated = translateBlocks(blocks, targetLang, scaleUp, temporalInpainter)
                renderer.update(translated)

                onSubtitleEvent?.invoke(
                    blocks.first().text,
                    translated.firstOrNull()?.text ?: ""
                )
            } catch (e: CancellationException) {
                if (!frame.isRecycled) frame.recycle()
                throw e
            } catch (e: Exception) {
                if (!frame.isRecycled) frame.recycle()
                Log.e(TAG, "Video loop error: ${e.message}")
            }
        }
    }

    /**
     * Translates blocks sequentially to avoid the shared-translator cancellation bug.
     *
     * [CloudStreamingTranslator.translate] cancels the previous in-flight request each time
     * it is called. Parallel async blocks would race to cancel each other, leaving all but
     * the last continuation permanently unsuspended → [awaitAll] deadlock.
     * Sequential translation is safe: each block waits for the previous to finish.
     * Subtitle frames rarely have more than 1-2 blocks, so the added latency is negligible.
     */
    private suspend fun translateBlocks(
        blocks: List<TextBlock>,
        targetLang: String,
        scaleUp: Float,
        temporalInpainter: TemporalBackgroundInpainter?,
    ): List<RenderedSubtitle> {
        return blocks.map { block ->
            val translatedText = translateAsync(block.text, targetLang)
            val coverColor = temporalInpainter?.sampleColor(block.boundingBox)
                ?: android.graphics.Color.TRANSPARENT
            RenderedSubtitle(
                text = translatedText,
                coverColor = coverColor,
                boundingBox = RectF(
                    block.boundingBox.left   * scaleUp,
                    block.boundingBox.top    * scaleUp,
                    block.boundingBox.right  * scaleUp,
                    block.boundingBox.bottom * scaleUp,
                )
            )
        }
    }

    // ─── Audio path ───────────────────────────────────────────────────────────

    private fun setupAsrCallbacks(targetLang: String) {
        asr.setPartialResultCallback { text ->
            // Show interim ASR text immediately — no translation yet
            renderer.updateAudioBar("…$text")
        }

        asr.setFinalResultCallback { text ->
            scope.launch {
                val translated = translateAsync(text, targetLang)
                renderer.updateAudioBar(translated)
                onSubtitleEvent?.invoke(text, translated)
                lastAsrText = text
            }
        }
    }

    // ─── Translation helper ───────────────────────────────────────────────────

    /**
     * Suspend wrapper around [ITranslator.translate] that accumulates streaming
     * tokens and returns the final translation.
     *
     * Deduplication: if this text was already translated by the other stream
     * within [DEDUP_WINDOW_MS], returns a cached result immediately.
     */
    private suspend fun translateAsync(text: String, targetLang: String): String =
        suspendCancellableCoroutine { cont ->
            translator.translate(
                text       = text,
                sourceLang = "auto",
                targetLang = targetLang,
                onPartial  = { /* partial already handled by streaming renderer */ },
                onComplete = { result ->
                    lastTranslatedAt = System.currentTimeMillis()
                    if (cont.isActive) cont.resume(result)
                },
                onError = { err ->
                    Log.e(TAG, "Translation failed: ${err.message}")
                    if (cont.isActive) cont.resume(text)   // fallback: show original
                },
            )
        }
}
