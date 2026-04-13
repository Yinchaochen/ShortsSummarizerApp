package com.uchia.capture

import android.content.Intent
import android.graphics.RectF

// ─── Capture ──────────────────────────────────────────────────────────────────

data class CaptureConfig(
    val resultCode: Int,
    val resultData: Intent,
    /** Downscale factor applied to frames before OCR. 0.5 = half resolution. */
    val scaleFactor: Float = 0.5f,
)

data class PCMChunk(
    val samples: ShortArray,
    val sampleRate: Int,
)

// ─── OCR ──────────────────────────────────────────────────────────────────────

data class TextBlock(
    val text: String,
    val boundingBox: RectF,
    val confidence: Float = 0f,
)

// ─── Session ──────────────────────────────────────────────────────────────────

data class SessionConfig(
    val targetLang: String,
    val enableVideo: Boolean = true,
    val enableAudio: Boolean = false,
    /**
     * BCP-47 code of the language displayed in the video subtitles.
     * Determines which MLKit OCR script is loaded.
     * "auto" falls back to Latin.
     */
    val sourceLang: String = "auto",
)

// ─── Renderer ─────────────────────────────────────────────────────────────────

data class RenderedSubtitle(
    /** Translated text to display. */
    val text: String,
    /**
     * Position in real screen pixels (already scaled from capture space).
     * The renderer maps these coordinates to its overlay window.
     */
    val boundingBox: RectF,
    /**
     * Background cover color sampled from the temporal background model.
     * The renderer draws a solid rectangle of this color over the original text
     * before rendering [text] on top.
     * [android.graphics.Color.TRANSPARENT] means no cover patch (Phase 1 fallback).
     */
    val coverColor: Int = android.graphics.Color.TRANSPARENT,
)
