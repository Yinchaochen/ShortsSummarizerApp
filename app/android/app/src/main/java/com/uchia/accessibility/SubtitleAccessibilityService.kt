package com.uchia.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * Phase 2+3: Reads subtitle text from the View layer of foreground video apps,
 * translates it with ML Kit (on-device, offline), and displays the result via
 * a TYPE_ACCESSIBILITY_OVERLAY window — no extra permissions required.
 */
class SubtitleAccessibilityService : AccessibilityService() {

    companion object {
        const val TAG = "UchiaSubtitle"
        const val PREFS_NAME = "uchia_prefs"
        const val KEY_TARGET_LANGUAGE = "target_language"
        const val KEY_OVERLAY_ENABLED = "overlay_enabled"

        val TARGET_PACKAGES = setOf(
            "com.zhiliaoapp.musically",        // TikTok (global)
            "com.ss.android.ugc.trill",        // TikTok (some regions)
            "com.instagram.android",            // Instagram Reels
            "com.google.android.youtube",       // YouTube / Shorts
            "com.netflix.mediaclient",          // Netflix
            "com.amazon.avod.thirdpartyclient", // Prime Video
        )

        /** BubbleModule registers here to receive raw subtitle events for JS layer. */
        var listener: SubtitleListener? = null
    }

    interface SubtitleListener {
        fun onSubtitleDetected(text: String, sourcePackage: String)
    }

    // ─── State ────────────────────────────────────────────────────────────────

    private var lastText = ""
    private val overlay by lazy { TranslationOverlay(this) }

    /** Cached translator — reused while the language pair stays the same. */
    private var translator: Translator? = null
    private var activeLangPair = "" // "src->target"

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
        Log.d(TAG, "Service connected")
    }

    override fun onInterrupt() {
        overlay.hide()
        Log.d(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        overlay.hide()
        translator?.close()
    }

    // ─── Event handling ───────────────────────────────────────────────────────

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString() ?: return
        if (pkg !in TARGET_PACKAGES) return
        if (!isOverlayEnabled()) return

        val root = rootInActiveWindow ?: return
        try {
            val screenHeight = resources.displayMetrics.heightPixels
            val subtitle = findSubtitle(root, screenHeight)

            if (subtitle != null && subtitle != lastText) {
                lastText = subtitle
                // Forward raw text to JS layer (Phase 2 monitor card)
                listener?.onSubtitleDetected(subtitle, pkg)
                // Translate and show overlay (Phase 3)
                translateAndShow(subtitle)
            }
        } finally {
            root.recycle()
        }
    }

    // ─── Translation pipeline ─────────────────────────────────────────────────

    private fun translateAndShow(text: String) {
        val targetLang = getTargetLanguage()

        LanguageIdentification.getClient()
            .identifyLanguage(text)
            .addOnSuccessListener { srcLang ->
                when {
                    srcLang == "und" -> {
                        // Language undetectable — show original
                        overlay.update(text)
                    }
                    srcLang == targetLang -> {
                        // Already in the target language — show as-is
                        overlay.update(text)
                    }
                    else -> {
                        // Need translation
                        val pair = "$srcLang->$targetLang"
                        acquireTranslator(srcLang, targetLang, pair, text) { t ->
                            t.translate(text)
                                .addOnSuccessListener { translated ->
                                    overlay.update(translated)
                                }
                                .addOnFailureListener {
                                    overlay.update(text)
                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                overlay.update(text)
            }
    }

    /**
     * Returns a ready-to-use [Translator] for the given language pair.
     * Caches the instance — avoids recreating the translator on every subtitle update.
     * Downloads the language model on first use (~30 MB per pair, needs internet once).
     * Falls back to showing [fallbackText] while downloading or on failure.
     */
    private fun acquireTranslator(
        src: String,
        target: String,
        pair: String,
        fallbackText: String,
        onReady: (Translator) -> Unit,
    ) {
        if (pair == activeLangPair && translator != null) {
            onReady(translator!!)
            return
        }

        translator?.close()
        activeLangPair = pair

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(src)
            .setTargetLanguage(target)
            .build()
        val t = Translation.getClient(options)
        translator = t

        // Show original text immediately while the model downloads
        overlay.update(fallbackText)

        t.downloadModelIfNeeded()
            .addOnSuccessListener { onReady(t) }
            .addOnFailureListener { e ->
                Log.e(TAG, "Model download failed for $pair: $e")
                // Fallback: show original subtitle text so the overlay is still useful
                overlay.update(fallbackText)
            }
    }

    // ─── Subtitle detection ───────────────────────────────────────────────────

    private fun findSubtitle(root: AccessibilityNodeInfo, screenHeight: Int): String? {
        // Collect (text, width) pairs; prefer the widest candidate (subtitle views span most of screen)
        val candidates = mutableListOf<Triple<String, Int, Int>>() // text, top, width
        collectCandidates(root, screenHeight, candidates)
        if (candidates.isEmpty()) return null
        // Primary sort: widest (subtitle spans screen); secondary: lowest on screen
        return candidates.maxWithOrNull(
            compareBy({ it.third }, { it.second })
        )?.first
    }

    private fun collectCandidates(
        node: AccessibilityNodeInfo,
        screenHeight: Int,
        out: MutableList<Triple<String, Int, Int>>,
    ) {
        val text = node.text?.toString()?.trim()
        if (!text.isNullOrEmpty() && isSubtitleCandidate(node, text, screenHeight)) {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            out.add(Triple(text, bounds.top, bounds.width()))
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
        // Length gate: subtitles are rarely < 8 or > 200 chars
        if (text.length < 8 || text.length > 200) return false

        // Must contain at least one letter (filters out timestamps, icon labels)
        if (!text.any { it.isLetter() }) return false

        // All-uppercase text is almost always a UI button or header, not a subtitle
        if (text == text.uppercase() && text.length < 30) return false

        // Single-word with no punctuation is a UI label (button, tab, username etc.)
        // Allow short text only if it ends with sentence punctuation like "Yes." "Okay."
        val hasSpace = text.contains(' ')
        val hasSentencePunct = text.endsWith('.') || text.endsWith('?') || text.endsWith('!')
        if (!hasSpace && !hasSentencePunct) return false

        // Must be in the bottom half of the screen
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        if (bounds.isEmpty) return false
        if (bounds.top < screenHeight * 0.50f) return false

        // Reject if the view spans the full width (likely a full-screen overlay UI element)
        val screenWidth = resources.displayMetrics.widthPixels
        if (bounds.width() > screenWidth * 0.95f && bounds.height() > screenHeight * 0.15f) return false

        return true
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
