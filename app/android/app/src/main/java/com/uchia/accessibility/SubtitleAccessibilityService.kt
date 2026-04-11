package com.uchia.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.uchia.capture.PositionalOverlayView
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * Reads subtitle text from foreground video apps via the accessibility tree,
 * translates each block with ML Kit (on-device), and renders positioned bubbles
 * at the exact screen coordinates of the original subtitles.
 */
class SubtitleAccessibilityService : AccessibilityService() {

    companion object {
        const val TAG = "UchiaSubtitle"
        const val PREFS_NAME = "uchia_prefs"
        const val KEY_TARGET_LANGUAGE = "target_language"
        const val KEY_OVERLAY_ENABLED = "overlay_enabled"

        /** Text stable for longer than this is a UI element, not a subtitle. */
        private const val STABLE_THRESHOLD_MS = 3_000L
        private const val CACHE_SIZE = 100
        private const val MAX_BUBBLES = 5

        val TARGET_PACKAGES = setOf(
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "com.instagram.android",
            "com.google.android.youtube",
            "com.netflix.mediaclient",
            "com.amazon.avod.thirdpartyclient",
        )

        /** BubbleModule registers here to receive subtitle events for the JS layer. */
        var listener: SubtitleListener? = null
    }

    interface SubtitleListener {
        fun onSubtitleDetected(text: String, sourcePackage: String)
    }

    // ─── State ────────────────────────────────────────────────────────────────

    /**
     * Use TYPE_ACCESSIBILITY_OVERLAY — no SYSTEM_ALERT_WINDOW needed,
     * permission is covered by the accessibility service grant itself.
     */
    private val overlay by lazy {
        PositionalOverlayView(this, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
    }

    /** text → first-seen timestamp. Text stable > STABLE_THRESHOLD_MS is treated as UI. */
    private val stableTextTracker = Collections.synchronizedMap(mutableMapOf<String, Long>())

    /** LRU cache: original text → translated text. Avoids re-calling ML Kit for repeated lines. */
    private val translationCache = Collections.synchronizedMap(
        object : LinkedHashMap<String, String>(CACHE_SIZE, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<String, String>) = size > CACHE_SIZE
        }
    )

    /** One Translator instance per language pair "src->target". */
    private val translators = mutableMapOf<String, Translator>()

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                         AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
        overlay.show()
        Log.d(TAG, "Service connected — overlay ready")
    }

    override fun onInterrupt() {
        overlay.hide()
        Log.d(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        overlay.hide()
        translators.values.forEach { it.close() }
        translators.clear()
    }

    // ─── Event handling ───────────────────────────────────────────────────────

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString() ?: return
        if (pkg !in TARGET_PACKAGES) return
        if (!isOverlayEnabled()) return

        val root = rootInActiveWindow ?: return
        try {
            val screenHeight = resources.displayMetrics.heightPixels
            val subtitles = findSubtitles(root, screenHeight)

            if (subtitles.isNotEmpty()) {
                listener?.onSubtitleDetected(subtitles.first().first, pkg)
                translateAndShowAll(subtitles)
            } else {
                overlay.update(emptyList())
            }
        } finally {
            root.recycle()
        }
    }

    // ─── Subtitle detection ───────────────────────────────────────────────────

    /**
     * Returns up to [MAX_BUBBLES] subtitle candidates as (text, screenBounds) pairs.
     * Candidates are sorted by width (widest = most likely to be a subtitle).
     */
    private fun findSubtitles(root: AccessibilityNodeInfo, screenHeight: Int): List<Pair<String, Rect>> {
        val candidates = mutableListOf<Triple<String, Rect, Int>>() // text, bounds, width
        collectCandidates(root, screenHeight, candidates)

        val screenWidth = resources.displayMetrics.widthPixels
        val now = System.currentTimeMillis()

        // Prune stale stable-text entries every cycle
        if (stableTextTracker.size > 200) {
            val cutoff = now - 30_000L
            stableTextTracker.entries.removeIf { it.value < cutoff }
        }

        return candidates
            .filter { (text, _, width) ->
                // Must span at least 30% of screen width (subtitles span most of the screen)
                width > screenWidth * 0.30f &&
                // Ignore text that has been on-screen for > 3 s (UI element, not subtitle)
                !isStableText(text, now)
            }
            .sortedByDescending { it.third }
            .take(MAX_BUBBLES)
            .map { Pair(it.first, it.second) }
    }

    private fun collectCandidates(
        node: AccessibilityNodeInfo,
        screenHeight: Int,
        out: MutableList<Triple<String, Rect, Int>>,
    ) {
        val text = node.text?.toString()?.trim()
        if (!text.isNullOrEmpty() && isSubtitleCandidate(node, text, screenHeight)) {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            // Store a copy of bounds — the original Rect may be recycled
            out.add(Triple(text, Rect(bounds), bounds.width()))
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                collectCandidates(child, screenHeight, out)
            } finally {
                child.recycle()
            }
        }
    }

    private fun isSubtitleCandidate(
        node: AccessibilityNodeInfo,
        text: String,
        screenHeight: Int,
    ): Boolean {
        // Length gate
        if (text.length < 8 || text.length > 200) return false
        // Must contain at least one letter
        if (!text.any { it.isLetter() }) return false
        // All-caps short text = UI button/header
        if (text == text.uppercase() && text.length < 30) return false
        // Single word with no sentence punctuation = UI label or username
        val hasSpace = text.contains(' ')
        val hasSentencePunct = text.last() in ".?!…"
        if (!hasSpace && !hasSentencePunct) return false

        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        if (bounds.isEmpty) return false
        // Must be in the bottom half of the screen
        if (bounds.top < screenHeight * 0.50f) return false
        // Reject full-screen-width very tall views (full-screen overlays)
        val screenWidth = resources.displayMetrics.widthPixels
        if (bounds.width() > screenWidth * 0.95f && bounds.height() > screenHeight * 0.15f) return false

        return true
    }

    private fun isStableText(text: String, now: Long): Boolean {
        val firstSeen = stableTextTracker.getOrPut(text) { now }
        return (now - firstSeen) > STABLE_THRESHOLD_MS
    }

    // ─── Translation pipeline ─────────────────────────────────────────────────

    /**
     * Translates all [blocks] in parallel. When every block is resolved
     * (from cache or ML Kit), updates the overlay with all bubbles at once.
     */
    private fun translateAndShowAll(blocks: List<Pair<String, Rect>>) {
        val targetLang = getTargetLanguage()
        val results = Collections.synchronizedList(
            mutableListOf<PositionalOverlayView.TranslatedBlock>()
        )
        val pending = AtomicInteger(blocks.size)

        fun commit(translatedText: String, bounds: Rect) {
            results.add(
                PositionalOverlayView.TranslatedBlock(
                    translatedText,
                    bounds.left, bounds.top, bounds.right, bounds.bottom,
                )
            )
            if (pending.decrementAndGet() == 0) {
                overlay.update(results)
            }
        }

        for ((text, bounds) in blocks) {
            // Cache hit — instant, no ML Kit call needed
            val cached = translationCache[text]
            if (cached != null) {
                commit(cached, bounds)
                continue
            }

            // Identify language, then translate
            LanguageIdentification.getClient()
                .identifyLanguage(text)
                .addOnSuccessListener { srcLang ->
                    if (srcLang == "und" || srcLang == targetLang) {
                        // Already in target language or undetectable — show as-is
                        translationCache[text] = text
                        commit(text, bounds)
                    } else {
                        val pair = "$srcLang->$targetLang"
                        acquireTranslator(srcLang, targetLang, pair) { translator ->
                            translator.translate(text)
                                .addOnSuccessListener { translated ->
                                    translationCache[text] = translated
                                    commit(translated, bounds)
                                }
                                .addOnFailureListener {
                                    commit(text, bounds) // fallback: show original
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    commit(text, bounds) // fallback: show original
                }
        }
    }

    /**
     * Returns a ready [Translator] for the given language pair.
     * Reuses cached instances to avoid recreating the ML Kit client on every event.
     * Downloads the translation model on first use (~30 MB per pair, one-time).
     */
    private fun acquireTranslator(
        src: String,
        target: String,
        pair: String,
        onReady: (Translator) -> Unit,
    ) {
        val existing = translators[pair]
        if (existing != null) {
            onReady(existing)
            return
        }

        val t = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(src)
                .setTargetLanguage(target)
                .build()
        )
        translators[pair] = t

        t.downloadModelIfNeeded()
            .addOnSuccessListener { onReady(t) }
            .addOnFailureListener { e ->
                Log.e(TAG, "Model download failed for $pair: $e")
            }
    }

    // ─── Preferences ─────────────────────────────────────────────────────────

    private fun getTargetLanguage(): String =
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TARGET_LANGUAGE, TranslateLanguage.ENGLISH)
            ?: TranslateLanguage.ENGLISH

    private fun isOverlayEnabled(): Boolean =
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_OVERLAY_ENABLED, true)
}
