package com.uchia.capture

/**
 * Abstracts the subtitle overlay renderer.
 *
 * Android implementation: [PositionalOverlayRenderer] (WindowManager overlay)
 * iOS implementation (future): UIKitOverlayRenderer
 *
 * All methods are thread-safe — implementations must marshal to the main thread
 * internally so callers don't need to care.
 */
interface ISubtitleRenderer {
    /** Attach the overlay window. Must be called before [update]. */
    fun show()

    /**
     * Replace all visible subtitles with [subtitles].
     * Pass an empty list to clear the overlay.
     */
    fun update(subtitles: List<RenderedSubtitle>)

    /**
     * Show or update the audio transcription bar.
     * Pass null or blank to hide it.
     */
    fun updateAudioBar(text: String?)

    /** Remove all subtitles and detach the overlay window. */
    fun hide()

    fun release()
}
