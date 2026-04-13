package com.uchia.capture

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*

/**
 * [IAsrEngine] adapter that wraps the existing [SherpaAsrManager].
 *
 * Responsibilities:
 *  - Lazy-init the recogniser from on-disk model files (via [SherpaModelManager])
 *  - Convert PCM ShortArray → FloatArray expected by Sherpa
 *  - Forward partial / final results through the [IAsrEngine] callback contract
 *
 * If models are not yet downloaded, [feedAudio] is silently a no-op until
 * [isReady] returns true. The user should download models via the JS API
 * before enabling audio translation.
 */
class SherpaAsrAdapter(private val context: Context) : IAsrEngine {

    companion object {
        private const val TAG = "SherpaAsrAdapter"
    }

    private val modelManager = SherpaModelManager(context)
    private val manager      = SherpaAsrManager()
    private val scope        = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var partialCallback: ((String) -> Unit)? = null
    private var finalCallback:   ((String) -> Unit)? = null

    // ─── IAsrEngine ───────────────────────────────────────────────────────────

    override fun start() {
        if (manager.isReady()) return
        if (!modelManager.areModelsReady()) {
            Log.d(TAG, "ASR models not downloaded — audio path will be silent until ready")
            return
        }
        scope.launch {
            try {
                manager.init(
                    modelManager.encoderFile,
                    modelManager.decoderFile,
                    modelManager.joinerFile,
                    modelManager.tokensFile,
                )
                manager.onPartialResult = { text -> partialCallback?.invoke(text) }
                manager.onFinalResult   = { text -> finalCallback?.invoke(text) }
                Log.d(TAG, "Sherpa ASR initialised")
            } catch (e: Exception) {
                Log.e(TAG, "Sherpa init failed: ${e.message}")
            }
        }
    }

    override fun stop() {
        manager.flush()
    }

    override fun feedAudio(chunk: PCMChunk) {
        if (!manager.isReady()) return
        // Sherpa expects Float32 normalised to [-1, 1]
        val floats = FloatArray(chunk.samples.size) { i ->
            chunk.samples[i] / 32768f
        }
        manager.acceptWaveform(floats)
    }

    override fun flush() {
        if (manager.isReady()) manager.flush()
    }

    override fun setPartialResultCallback(cb: (String) -> Unit) {
        partialCallback = cb
        manager.onPartialResult = cb
    }

    override fun setFinalResultCallback(cb: (String) -> Unit) {
        finalCallback = cb
        manager.onFinalResult = cb
    }

    override fun isReady(): Boolean = manager.isReady()

    override fun release() {
        scope.cancel()
        manager.close()
    }
}
