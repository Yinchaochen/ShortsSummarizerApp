package com.uchia.capture

import android.util.Log
import com.k2fsa.sherpa.onnx.*
import java.io.File

/**
 * Wraps Sherpa-ONNX 1.12.x OnlineRecognizer for streaming speech-to-text.
 *
 * Model: sherpa-onnx-streaming-zipformer-en-20M int8 (~44 MB).
 * Requires sherpa-onnx-1.12.36.aar in app/libs/.
 */
class SherpaAsrManager {

    companion object {
        const val TAG = "SherpaASR"
    }

    private var recognizer: OnlineRecognizer? = null
    private var stream: OnlineStream? = null

    var onPartialResult: ((String) -> Unit)? = null
    var onFinalResult:   ((String) -> Unit)? = null

    fun isReady() = recognizer != null

    fun init(
        encoderFile: File,
        decoderFile: File,
        joinerFile:  File,
        tokensFile:  File,
    ) {
        try {
            val config = OnlineRecognizerConfig(
                featConfig = FeatureConfig(sampleRate = 16000, featureDim = 80),
                modelConfig = OnlineModelConfig(
                    transducer = OnlineTransducerModelConfig(
                        encoder = encoderFile.absolutePath,
                        decoder = decoderFile.absolutePath,
                        joiner  = joinerFile.absolutePath,
                    ),
                    tokens    = tokensFile.absolutePath,
                    modelType = "zipformer",
                ),
                endpointConfig = EndpointConfig(
                    rule1 = EndpointRule(mustContainNonSilence = false, minTrailingSilence = 2.4f, minUtteranceLength = 0f),
                    rule2 = EndpointRule(mustContainNonSilence = true,  minTrailingSilence = 1.2f, minUtteranceLength = 0f),
                    rule3 = EndpointRule(mustContainNonSilence = false, minTrailingSilence = 0f,   minUtteranceLength = 20f),
                ),
                enableEndpoint  = true,
                decodingMethod  = "greedy_search",
                maxActivePaths  = 4,
            )
            // Pass null AssetManager — we're loading from absolute file paths, not assets
            recognizer = OnlineRecognizer(null, config)
            stream     = recognizer!!.createStream("")
            Log.d(TAG, "SherpaASR ready")
        } catch (e: Exception) {
            Log.e(TAG, "Sherpa init failed: ${e.message}", e)
        }
    }

    /**
     * Feed a 16 kHz mono Float32 PCM chunk.
     * Fires [onPartialResult] with interim text and [onFinalResult] on endpoint.
     */
    fun acceptWaveform(samples: FloatArray) {
        val rec = recognizer ?: return
        val str = stream     ?: return

        str.acceptWaveform(samples, 16_000)
        while (rec.isReady(str)) {
            rec.decode(str)
        }

        val text = rec.getResult(str).text.trim()
        if (text.isNotEmpty()) onPartialResult?.invoke(text)

        if (rec.isEndpoint(str)) {
            if (text.isNotEmpty()) onFinalResult?.invoke(text)
            rec.reset(str)
        }
    }

    /** Flush remaining audio and emit final result. */
    fun flush() {
        val rec = recognizer ?: return
        val str = stream     ?: return
        str.inputFinished()
        while (rec.isReady(str)) rec.decode(str)
        val text = rec.getResult(str).text.trim()
        if (text.isNotEmpty()) onFinalResult?.invoke(text)
    }

    fun close() {
        stream?.release()
        recognizer?.release()
        stream     = null
        recognizer = null
    }
}
