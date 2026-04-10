package com.uchia.bubble

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Content displayed inside an expanded Bubble.
 * Receives its type + data via Intent extras.
 *
 * Phase 2: replace Android Views with a WebView / React Native renderer
 * once the translation pipeline and pet assets are ready.
 */
class BubbleActivity : Activity() {

    companion object {
        const val EXTRA_TYPE   = "bubble_type"
        const val EXTRA_TEXT   = "bubble_text"
        const val EXTRA_PET_ID = "bubble_pet_id"
        const val TYPE_TRANSLATION = "translation"
        const val TYPE_PET         = "pet"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type   = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_TRANSLATION
        val text   = intent.getStringExtra(EXTRA_TEXT) ?: ""

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF0f0f1a.toInt())
            setPadding(40, 32, 40, 32)
        }

        when (type) {
            TYPE_TRANSLATION -> buildTranslationView(root, text)
            TYPE_PET         -> buildPetView(root, text)
        }

        setContentView(ScrollView(this).apply { addView(root) })
    }

    // ─── Translation layout ───────────────────────────────────────────────────

    private fun buildTranslationView(root: LinearLayout, text: String) {
        root.addView(TextView(this).apply {
            this.text = "TRANSLATION"
            textSize = 10f
            setTextColor(0xFF7170ff.toInt())
            letterSpacing = 0.15f
            setPadding(0, 0, 0, 12)
        })
        root.addView(TextView(this).apply {
            this.text = text.ifBlank { "—" }
            textSize = 15f
            setTextColor(0xFFf7f8f8.toInt())
            lineHeight = (textSize * 1.6f).toInt()
        })
    }

    // ─── Pet layout ──────────────────────────────────────────────────────────

    private fun buildPetView(root: LinearLayout, petName: String) {
        root.gravity = Gravity.CENTER

        root.addView(TextView(this).apply {
            text = "🐾"
            textSize = 52f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 8)
        })
        root.addView(TextView(this).apply {
            text = petName
            textSize = 17f
            setTextColor(0xFFf7f8f8.toInt())
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "Tap to interact"
            textSize = 12f
            setTextColor(0xFF62666d.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 4, 0, 0)
        })
    }
}
