package com.uchia.capture

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Extracts text blocks from a full screen-capture frame using ML Kit Text Recognition
 * (on-device, offline). Each block carries the text content and its bounding box in the
 * capture frame's coordinate space — callers must scale to real screen coordinates.
 *
 * CJK recognizers are lazy so they're only initialised when the user actually needs them.
 */
class OcrProcessor {

    // ─── Result types ─────────────────────────────────────────────────────────

    data class OcrResult(
        /** All detected blocks joined into one string, for deduplication. */
        val text: String,
        /** Individual blocks with positions — used for positional overlay rendering. */
        val blocks: List<TextBlock>,
    )

    data class TextBlock(
        val text: String,
        /** Bounding box in the *capture frame* coordinate space (not real screen pixels). */
        val bounds: Rect,
        val confidence: Float,
    )

    // ─── Constants ────────────────────────────────────────────────────────────

    companion object {
        /** Minimum text length to consider as meaningful content. */
        private const val MIN_TEXT_LENGTH = 4

        /** Upper bound — longer blocks are likely full UI sections, not discrete labels. */
        private const val MAX_TEXT_LENGTH = 300
    }

    // ─── Recognizers ──────────────────────────────────────────────────────────

    private val latinRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val chineseRecognizer by lazy {
        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    }
    private val japaneseRecognizer by lazy {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }
    private val koreanRecognizer by lazy {
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Run OCR on the full [bitmap]. Returns null if no meaningful text is found.
     *
     * @param bitmap     The capture frame at reduced resolution (SCALE_FACTOR applied).
     * @param ocrScript  Which ML Kit model to use: "latin" | "chinese" | "japanese" | "korean"
     */
    suspend fun processFrame(bitmap: Bitmap, ocrScript: String = "latin"): OcrResult? {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = when (ocrScript) {
            "chinese"  -> chineseRecognizer
            "japanese" -> japaneseRecognizer
            "korean"   -> koreanRecognizer
            else       -> latinRecognizer
        }

        return suspendCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val blocks = visionText.textBlocks
                        .filter { block ->
                            val t = block.text.trim()
                            t.length in MIN_TEXT_LENGTH..MAX_TEXT_LENGTH &&
                                t.any { it.isLetter() }
                        }
                        .map { block ->
                            TextBlock(
                                text = block.text.trim(),
                                bounds = block.boundingBox ?: Rect(),
                                confidence = block.lines.firstOrNull()?.confidence ?: 0f,
                            )
                        }

                    if (blocks.isEmpty()) {
                        cont.resume(null)
                    } else {
                        cont.resume(OcrResult(
                            text = blocks.joinToString("\n") { it.text },
                            blocks = blocks,
                        ))
                    }
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    fun close() {
        latinRecognizer.close()
    }
}
