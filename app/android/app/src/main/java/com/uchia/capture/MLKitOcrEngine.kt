package com.uchia.capture

import android.graphics.Bitmap
import android.graphics.RectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * [IOcrEngine] backed by Google MLKit Text Recognition v2.
 *
 * Scans the full bitmap — no region assumptions. MLKit's internal two-stage
 * detection pipeline (detect → recognise) handles arbitrary text positions,
 * including stylised, glowing, or animated subtitles.
 *
 * Script is selected at construction time; swap the recogniser to cover
 * Chinese, Japanese, Korean, or Devanagari in addition to Latin.
 *
 * System UI chrome (status bar, nav bar) is filtered by [filterChrome] using
 * frame-relative proportions — this is UI noise removal, NOT a subtitle
 * position assumption.
 */
class MLKitOcrEngine(script: Script = Script.LATIN) : IOcrEngine {

    enum class Script { LATIN, CHINESE, JAPANESE, KOREAN, DEVANAGARI }

    private val recognizer: TextRecognizer = when (script) {
        Script.LATIN      -> TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        Script.CHINESE    -> TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        Script.JAPANESE   -> TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        Script.KOREAN     -> TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        Script.DEVANAGARI -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
    }

    // ─── IOcrEngine ───────────────────────────────────────────────────────────

    override suspend fun detect(frame: Bitmap): List<TextBlock> {
        val image  = InputImage.fromBitmap(frame, 0)
        val result = recognizer.process(image).await()

        return result.textBlocks
            .flatMap { block -> block.lines }
            .mapNotNull { line ->
                val box = line.boundingBox ?: return@mapNotNull null
                TextBlock(
                    text        = line.text,
                    boundingBox = RectF(box),
                    confidence  = 0f,   // MLKit does not expose per-line confidence
                )
            }
            .let { filterChrome(it, frame.width, frame.height) }
    }

    override fun release() {
        recognizer.close()
    }

    // ─── Chrome filter ────────────────────────────────────────────────────────

    /**
     * Remove blocks that fall in the system UI chrome regions.
     * These are Android status bar (top 8%) and navigation bar (bottom 10%),
     * measured as proportions of the capture frame — independent of screen size.
     *
     * This is NOT a subtitle region filter. It removes OS-level noise so we
     * don't waste translation budget on "10:42" or battery indicators.
     */
    private fun filterChrome(
        blocks: List<TextBlock>,
        frameW: Int,
        frameH: Int,
    ): List<TextBlock> {
        val topExclude    = frameH * 0.08f
        val bottomExclude = frameH * 0.95f   // only exclude nav bar, not subtitle area
        val minBlockH     = 10

        return blocks.filter { block ->
            block.boundingBox.top    >= topExclude    &&
            block.boundingBox.bottom <= bottomExclude &&
            (block.boundingBox.bottom - block.boundingBox.top) >= minBlockH
        }
    }
}

// ─── MLKit Task → suspend extension ──────────────────────────────────────────

private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }
