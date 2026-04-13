package com.uchia.capture

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * [ITranslator] backed by a streaming cloud API (Claude / Gemini).
 *
 * Delivers incremental tokens via [onPartial] as they stream in, then
 * fires [onComplete] with the full assembled translation.
 *
 * To switch providers (e.g. Claude → Gemini) change [buildRequest] only —
 * the streaming SSE parser and callback contract stay the same.
 *
 * Deduplication: if [translate] is called with the same text + lang pair
 * before the previous request finishes, the in-flight request is cancelled
 * and a new one starts. This prevents stale translations from arriving late.
 */
class CloudStreamingTranslator(
    private val apiKey: String,
    private val apiUrl: String = "https://api.anthropic.com/v1/messages",
    private val model: String = "claude-haiku-4-5-20251001",
) : ITranslator {

    companion object {
        private const val TAG = "Translator"
    }

    private val client = OkHttpClient.Builder()
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentCall: Call? = null
    private var currentJob: Job? = null

    // ─── ITranslator ──────────────────────────────────────────────────────────

    override fun translate(
        text: String,
        sourceLang: String,
        targetLang: String,
        onPartial: (String) -> Unit,
        onComplete: (String) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        currentJob?.cancel()
        currentCall?.cancel()

        currentJob = scope.launch {
            try {
                streamTranslation(text, sourceLang, targetLang, onPartial, onComplete)
            } catch (e: CancellationException) {
                // Normal cancellation — do not fire onError
            } catch (e: Exception) {
                Log.e(TAG, "Translation error: ${e.message}")
                onError(e)
            }
        }
    }

    override fun cancel() {
        currentJob?.cancel()
        currentCall?.cancel()
    }

    override fun release() {
        cancel()
        scope.cancel()
        client.dispatcher.executorService.shutdown()
    }

    // ─── Streaming ────────────────────────────────────────────────────────────

    private suspend fun streamTranslation(
        text: String,
        sourceLang: String,
        targetLang: String,
        onPartial: (String) -> Unit,
        onComplete: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val request = buildRequest(text, sourceLang, targetLang)
        val call = client.newCall(request)
        currentCall = call

        val response = suspendCancellableCoroutine<Response> { cont ->
            cont.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) { cont.resume(response) }
                override fun onFailure(call: Call, e: IOException) { cont.resumeWithException(e) }
            })
        }

        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}: ${response.body?.string()}")
        }

        val accumulated = StringBuilder()
        response.body?.source()?.use { source ->
            while (!source.exhausted() && isActive) {
                val line = source.readUtf8Line() ?: break
                if (!line.startsWith("data: ")) continue
                val data = line.removePrefix("data: ").trim()
                if (data == "[DONE]") break

                val token = parseToken(data) ?: continue
                accumulated.append(token)
                onPartial(accumulated.toString())
            }
        }

        onComplete(accumulated.toString())
    }

    // ─── Request builder (Claude Messages API) ────────────────────────────────

    private fun buildRequest(text: String, sourceLang: String, targetLang: String): Request {
        val prompt = buildPrompt(text, sourceLang, targetLang)

        val body = JSONObject().apply {
            put("model", model)
            put("max_tokens", 1024)
            put("stream", true)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("system", "You are a subtitle translator. Output ONLY the translated text — no explanations, no quotes, no punctuation changes unless required by the target language.")
        }.toString()

        return Request.Builder()
            .url(apiUrl)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
    }

    private fun buildPrompt(text: String, sourceLang: String, targetLang: String): String =
        "Translate the following from $sourceLang to $targetLang:\n\n$text"

    // ─── SSE token parser ─────────────────────────────────────────────────────

    private fun parseToken(data: String): String? {
        return try {
            val json = JSONObject(data)
            val type = json.optString("type")
            when (type) {
                "content_block_delta" -> {
                    json.optJSONObject("delta")?.optString("text")
                }
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}

