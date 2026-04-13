package com.uchia.capture

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Downloads and caches Sherpa-ONNX streaming Zipformer ASR model files.
 *
 * Model: sherpa-onnx-streaming-zipformer-en-20M-2023-02-17 (int8 quantized)
 * Source: https://huggingface.co/csukuangfj/sherpa-onnx-streaming-zipformer-en-20M-2023-02-17
 * Total size: ~44 MB (encoder 42.8 MB + decoder 539 KB + joiner 260 KB + tokens 5 KB)
 *
 * For other languages, swap the BASE_URL and file list to a different model from:
 * https://k2-fsa.github.io/sherpa/onnx/pretrained_models/online-transducer/zipformer-transducer-models.html
 */
class SherpaModelManager(private val context: Context) {

    companion object {
        const val TAG = "SherpaModels"

        private const val HF_BASE =
            "https://huggingface.co/csukuangfj/" +
            "sherpa-onnx-streaming-zipformer-en-20M-2023-02-17/resolve/main"

        // int8 quantized — ~44 MB total vs ~92 MB for fp32
        const val ENCODER_URL  = "$HF_BASE/encoder-epoch-99-avg-1.int8.onnx"
        const val DECODER_URL  = "$HF_BASE/decoder-epoch-99-avg-1.int8.onnx"
        const val JOINER_URL   = "$HF_BASE/joiner-epoch-99-avg-1.int8.onnx"
        const val TOKENS_URL   = "$HF_BASE/tokens.txt"

        const val ENCODER_FILE = "sherpa_encoder.int8.onnx"
        const val DECODER_FILE = "sherpa_decoder.int8.onnx"
        const val JOINER_FILE  = "sherpa_joiner.int8.onnx"
        const val TOKENS_FILE  = "sherpa_tokens.txt"
    }

    private val modelDir = File(context.filesDir, "asr_models").also { it.mkdirs() }
    private val scope     = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val encoderFile: File get() = File(modelDir, ENCODER_FILE)
    val decoderFile: File get() = File(modelDir, DECODER_FILE)
    val joinerFile:  File get() = File(modelDir, JOINER_FILE)
    val tokensFile:  File get() = File(modelDir, TOKENS_FILE)

    /** True when all four files exist and are non-empty. */
    fun areModelsReady(): Boolean =
        encoderFile.isNonEmpty() && decoderFile.isNonEmpty() &&
        joinerFile.isNonEmpty()  && tokensFile.isNonEmpty()

    private fun File.isNonEmpty() = exists() && length() > 0L

    /**
     * Download model files in sequence (encoder is large, do it last so partial
     * downloads are detected correctly by [areModelsReady]).
     *
     * @param onProgress (downloadedFiles, totalFiles) — fired after each file.
     * @param onComplete (success) — fired when all downloads complete or any fails.
     */
    fun downloadModels(
        onProgress: (Int, Int) -> Unit,
        onComplete: (Boolean) -> Unit,
    ) {
        scope.launch {
            val files = listOf(
                TOKENS_URL  to tokensFile,
                DECODER_URL to decoderFile,
                JOINER_URL  to joinerFile,
                ENCODER_URL to encoderFile,   // largest last
            )
            val total = files.size
            var allOk = true

            files.forEachIndexed { idx, (url, dest) ->
                if (dest.isNonEmpty()) {
                    Log.d(TAG, "${dest.name} already cached.")
                    onProgress(idx + 1, total)
                    return@forEachIndexed
                }
                try {
                    Log.d(TAG, "Downloading ${dest.name} (~${estimatedMb(url)} MB)…")
                    downloadFile(url, dest)
                    Log.d(TAG, "${dest.name} done (${dest.length() / 1024} KB)")
                    onProgress(idx + 1, total)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to download ${dest.name}: $e")
                    dest.delete()
                    allOk = false
                }
            }
            onComplete(allOk)
        }
    }

    private fun estimatedMb(url: String) = when {
        "encoder" in url -> "43"
        "decoder" in url -> "0.5"
        "joiner"  in url -> "0.3"
        else             -> "<0.1"
    }

    private fun downloadFile(urlStr: String, dest: File) {
        val tmp = File(dest.parent, "${dest.name}.tmp")
        try {
            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.connectTimeout = 20_000
            conn.readTimeout    = 120_000
            conn.connect()
            if (conn.responseCode != HttpURLConnection.HTTP_OK)
                throw Exception("HTTP ${conn.responseCode}")
            conn.inputStream.use { input ->
                tmp.outputStream().use { out -> input.copyTo(out) }
            }
            tmp.renameTo(dest)
        } catch (e: Exception) {
            tmp.delete()
            throw e
        }
    }

    fun cancel() = scope.cancel()
}
