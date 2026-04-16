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
    /**
     * Minimum ms between translation calls.
     * Set to 0 for on-device translators (no rate limit).
     * Set to ~1500 for cloud APIs (50 req/min limit).
     */
    private val translationCooldownMs: Long = 0L,
) {

    companion object {
        private const val TAG = "Orchestrator"
        /** Frames awaiting OCR. Drops oldest if producer outruns consumer. */
        private const val FRAME_CHANNEL_CAPACITY = 4
        /** Blocks with fewer letters than this are treated as noise (icons, numbers). */
        private const val MIN_BLOCK_CHARS = 3
        /** Max blocks to translate per frame — keeps latency bounded. */
        private const val MAX_BLOCKS = 12
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

        Log.e(TAG, "Started — video=${config.enableVideo} audio=${config.enableAudio} target=${config.targetLang}")
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
        Log.e(TAG, "Stopped")
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
                val t0 = System.currentTimeMillis()
                val allBlocks = ocr.detect(frame)
                val tOcr = System.currentTimeMillis()

                // ── Region filter: keep video content zone, exclude chrome ──
                // Exclude: very top (status bar 8%), very bottom (username bar 8%),
                // right-side reaction column (right 18%).
                // Subtitles on TikTok/YouTube appear at 70–90% height — keep them.
                val frameW = frame.width.toFloat()
                val frameH = frame.height.toFloat()
                val blocks = allBlocks.filter { b ->
                    val cy = b.boundingBox.centerY()
                    val cx = b.boundingBox.centerX()
                    cy > frameH * 0.08f && cy < frameH * 0.92f && cx < frameW * 0.82f
                }
                Log.e(TAG, "OCR: ${tOcr - t0}ms  raw=${allBlocks.size} filtered=${blocks.size}")

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

                // Enforce cooldown (cloud API only — on-device translators use 0ms).
                if (translationCooldownMs > 0) {
                    val sinceLastTranslation = System.currentTimeMillis() - lastTranslatedAt
                    if (sinceLastTranslation < translationCooldownMs) {
                        Log.e(TAG, "skip: cooldown ${sinceLastTranslation}ms < ${translationCooldownMs}ms")
                        continue
                    }
                }

                Log.e(TAG, "translating: \"${fullText.take(60)}\"")
                val tTxStart = System.currentTimeMillis()
                val translated = translateBlocks(blocks, targetLang, scaleUp, temporalInpainter)
                Log.e(TAG, "translation done: ${System.currentTimeMillis() - tTxStart}ms")

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
     * Translate all blocks and return updated [RenderedSubtitle] list.
     *
     * Two paths depending on [ITranslator.supportsBatch]:
     *  - Cloud (supportsBatch=true):  one API call with numbered list, streaming partial updates.
     *  - MLKit (supportsBatch=false): each block translated in parallel, ~20ms total.
     *
     * Noise filtering: blocks shorter than [MIN_BLOCK_CHARS] or with no letters are skipped.
     * Block count is capped at [MAX_BLOCKS].
     */
    private suspend fun translateBlocks(
        blocks: List<TextBlock>,
        targetLang: String,
        scaleUp: Float,
        temporalInpainter: TemporalBackgroundInpainter?,
    ): List<RenderedSubtitle> {
        val meaningful = blocks
            .filter { it.text.length >= MIN_BLOCK_CHARS && it.text.any { c -> c.isLetter() } }
            .take(MAX_BLOCKS)

        if (meaningful.isEmpty()) return emptyList()

        // Build subtitle shells — show original text immediately while translation is in flight.
        val subtitles = meaningful.map { block ->
            RenderedSubtitle(
                text = block.text,
                coverColor = temporalInpainter?.sampleColor(block.boundingBox)
                    ?: android.graphics.Color.TRANSPARENT,
                boundingBox = RectF(
                    block.boundingBox.left   * scaleUp,
                    block.boundingBox.top    * scaleUp,
                    block.boundingBox.right  * scaleUp,
                    block.boundingBox.bottom * scaleUp,
                )
            )
        }.toMutableList()
        renderer.update(subtitles)

        if (translator.supportsBatch) {
            // ── Cloud path: one call, numbered list, streaming ─────────────
            val batchInput = meaningful.mapIndexed { i, b -> "${i + 1}. ${b.text}" }.joinToString("\n")
            val batchResult = translateAsync(batchInput, targetLang) { partial ->
                applyBatchResult(partial, subtitles)
                renderer.update(subtitles)
            }
            applyBatchResult(batchResult, subtitles)
            Log.e(TAG, "cloud batch translated ${meaningful.size} blocks")
        } else {
            // ── MLKit path: all blocks in parallel, no rate limit ──────────
            coroutineScope {
                meaningful.mapIndexed { i, block ->
                    async {
                        val translated = translateAsync(block.text, targetLang)
                        if (translated.isNotEmpty()) {
                            subtitles[i] = subtitles[i].copy(text = translated)
                        }
                    }
                }.forEach { it.await() }
            }
            Log.e(TAG, "mlkit parallel translated ${meaningful.size} blocks")
        }

        return subtitles
    }

    /** Parses "1. text\n2. text\n..." response and writes translations into [subtitles]. */
    private fun applyBatchResult(result: String, subtitles: MutableList<RenderedSubtitle>) {
        result.lines().forEach { line ->
            val match = Regex("""^(\d+)\.\s*(.+)""").find(line.trim()) ?: return@forEach
            val idx = match.groupValues[1].toIntOrNull()?.minus(1) ?: return@forEach
            val text = match.groupValues[2].trim()
            if (idx in subtitles.indices && text.isNotEmpty()) {
                subtitles[idx] = subtitles[idx].copy(text = text)
            }
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
     * Suspend wrapper around [ITranslator.translate].
     *
     * Streams partial tokens to [onPartial] for live overlay updates, then
     * returns the fully assembled translation on completion.
     * Falls back to the original [text] on error so the overlay always shows something.
     */
    private suspend fun translateAsync(
        text: String,
        targetLang: String,
        onPartial: (String) -> Unit = {},
    ): String = suspendCancellableCoroutine { cont ->
        translator.translate(
            text       = text,
            sourceLang = "auto",
            targetLang = targetLang,
            onPartial  = onPartial,
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
