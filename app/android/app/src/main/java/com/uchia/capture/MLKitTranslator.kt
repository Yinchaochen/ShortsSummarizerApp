package com.uchia.capture

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

/**
 * [ITranslator] backed by ML Kit on-device translation.
 *
 * Zero API cost, zero latency after model download, works fully offline.
 * Translation models are ~30MB per language pair and cached on device.
 *
 * "auto" source language: uses ML Kit Language ID to detect the source,
 * then caches the detected language for the session (avoids per-call overhead).
 *
 * Quality: comparable to Google Translate — good for UI text and short phrases.
 * For nuanced long-form text, prefer [CloudStreamingTranslator].
 */
class MLKitTranslator(
    private val targetLang: String,
) : ITranslator {

    companion object {
        private const val TAG = "MLKitTranslator"
        /** Fall back to English when language ID is inconclusive. */
        private const val FALLBACK_SOURCE = TranslateLanguage.ENGLISH
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val langId = LanguageIdentification.getClient()

    /** Cached translator clients keyed by source language code. */
    private val clients = mutableMapOf<String, com.google.mlkit.nl.translate.Translator>()

    /** Cached source language detected during this session (avoids re-identifying every call). */
    @Volatile private var detectedSourceLang: String? = null

    /** Set to true once the target model has been confirmed downloaded. */
    @Volatile private var modelReady = false

    // ─── ITranslator ──────────────────────────────────────────────────────────

    override fun translate(
        text: String,
        sourceLang: String,
        targetLang: String,
        onPartial: (String) -> Unit,
        onComplete: (String) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        scope.launch {
            try {
                val result = translateSuspend(text, sourceLang)
                onComplete(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Translation error: ${e.message}")
                onError(e)
            }
        }
    }

    override fun cancel() { /* ML Kit tasks are not cancellable mid-flight; scope cancel handles cleanup */ }

    override fun release() {
        scope.cancel()
        langId.close()
        clients.values.forEach { it.close() }
        clients.clear()
    }

    // ─── Internals ────────────────────────────────────────────────────────────

    private suspend fun translateSuspend(text: String, sourceLangHint: String): String {
        val resolvedSource = resolveSourceLang(text, sourceLangHint)
        val mlkitTarget   = toMlKitCode(this.targetLang)

        if (resolvedSource == mlkitTarget) return text   // already in target language

        val translator = getOrCreateClient(resolvedSource, mlkitTarget)
        ensureModelReady(translator)

        return translator.translate(text).await()
    }

    /** Resolve "auto" by running Language ID (result cached per session). */
    private suspend fun resolveSourceLang(text: String, hint: String): String {
        if (hint != "auto") return toMlKitCode(hint)
        detectedSourceLang?.let { return it }

        val identified = langId.identifyLanguage(text).await()
        val resolved = if (identified == "und" || identified.isEmpty()) FALLBACK_SOURCE else identified
        detectedSourceLang = resolved
        Log.e(TAG, "Language identified: $resolved")
        return resolved
    }

    private fun getOrCreateClient(src: String, tgt: String): com.google.mlkit.nl.translate.Translator {
        val key = "$src->$tgt"
        return clients.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(src)
                .setTargetLanguage(tgt)
                .build()
            Translation.getClient(options)
        }
    }

    private suspend fun ensureModelReady(translator: com.google.mlkit.nl.translate.Translator) {
        if (modelReady) return
        Log.e(TAG, "Downloading translation model…")
        val conditions = DownloadConditions.Builder().build()   // allow any network
        translator.downloadModelIfNeeded(conditions).await()
        modelReady = true
        Log.e(TAG, "Translation model ready")
    }

    /** Maps BCP-47 codes to ML Kit two-letter codes. */
    private fun toMlKitCode(code: String): String = when (code.lowercase().split("-")[0]) {
        "zh", "zho", "cmn" -> TranslateLanguage.CHINESE
        "en"               -> TranslateLanguage.ENGLISH
        "de"               -> TranslateLanguage.GERMAN
        "ja"               -> TranslateLanguage.JAPANESE
        "ko"               -> TranslateLanguage.KOREAN
        "fr"               -> TranslateLanguage.FRENCH
        "es"               -> TranslateLanguage.SPANISH
        "ru"               -> TranslateLanguage.RUSSIAN
        "ar"               -> TranslateLanguage.ARABIC
        "pt"               -> TranslateLanguage.PORTUGUESE
        "it"               -> TranslateLanguage.ITALIAN
        "tr"               -> TranslateLanguage.TURKISH
        "vi"               -> TranslateLanguage.VIETNAMESE
        "th"               -> TranslateLanguage.THAI
        "id"               -> TranslateLanguage.INDONESIAN
        "hi"               -> TranslateLanguage.HINDI
        else               -> code
    }
}
