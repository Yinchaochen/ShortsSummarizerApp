/**
 * Live Translation public API.
 *
 * Single strategy: ScreenCapture (MediaProjection → MLKit OCR + Sherpa ASR → Cloud translate).
 * No AccessibilityService — the pipeline is entirely self-contained.
 *
 * Usage:
 *   const lt = getLiveTranslation();
 *   await lt.requestPermissions();
 *   await lt.start({ targetLang: "zh", enableAudio: true, apiKey: "sk-..." });
 *   const unsub = lt.onSubtitle(({ original, translated }) => { ... });
 *   await lt.stop();
 *   unsub();
 */

import { Platform } from "react-native";
import { ScreenCaptureBridge, LiveSubtitleEvent, CaptionSegment, CaptionsResponse } from "./screen-capture-bridge";

// ─── Types ────────────────────────────────────────────────────────────────────

export type { LiveSubtitleEvent, CaptionSegment, CaptionsResponse };

export interface StartOptions {
  /** BCP-47 target language code, e.g. "zh", "de", "en" */
  targetLang: string;
  /**
   * BCP-47 code of the language used in the video subtitles.
   * Selects the MLKit OCR recognizer script on the native side.
   * "auto" (default) falls back to Latin / A–Z text.
   * Use "zh" for Chinese, "ja" for Japanese, "ko" for Korean, etc.
   */
  sourceLang?: string;
  /** Also run the audio ASR path alongside video OCR. Default: false */
  enableAudio?: boolean;
  /** Anthropic API key for cloud streaming translation */
  apiKey: string;
}

export interface LiveTranslation {
  /** True if this feature is available on the current device */
  isAvailable(): boolean;

  /** Request both screen capture and overlay permissions. Returns true if all granted. */
  requestPermissions(): Promise<boolean>;

  /** Start the live translation session. */
  start(options: StartOptions): Promise<void>;

  /** Stop the running session. */
  stop(): Promise<void>;

  /** True if a session is currently running. */
  isRunning(): Promise<boolean>;

  /** Subscribe to translated subtitle events. Returns an unsubscribe function. */
  onSubtitle(callback: (event: LiveSubtitleEvent) => void): () => void;

  // ─── ASR model management ────────────────────────────────────────────────
  /** True if Sherpa-ONNX ASR models are downloaded. Needed for enableAudio. */
  areAsrModelsReady(): Promise<boolean>;
  /** Download ASR models (~44 MB, one-time). */
  downloadAsrModels(): Promise<void>;
  /** Subscribe to ASR model download progress. */
  onAsrModelDownloadProgress(cb: (downloaded: number, total: number) => void): () => void;
}

// ─── Implementation ───────────────────────────────────────────────────────────

class ScreenCaptureLiveTranslation implements LiveTranslation {

  isAvailable(): boolean {
    return ScreenCaptureBridge.isAvailable();
  }

  async requestPermissions(): Promise<boolean> {
    const overlayGranted = await ScreenCaptureBridge.checkOverlayPermission();
    if (!overlayGranted) {
      ScreenCaptureBridge.requestOverlayPermission();
      return false;
    }
    return ScreenCaptureBridge.requestPermission();
  }

  async start(options: StartOptions): Promise<void> {
    const { targetLang, sourceLang = "auto", enableAudio = false, apiKey } = options;
    return ScreenCaptureBridge.start(targetLang, sourceLang, enableAudio, apiKey);
  }

  async stop(): Promise<void> {
    return ScreenCaptureBridge.stop();
  }

  async isRunning(): Promise<boolean> {
    return ScreenCaptureBridge.isRunning();
  }

  onSubtitle(callback: (event: LiveSubtitleEvent) => void): () => void {
    return ScreenCaptureBridge.onSubtitle(callback);
  }

  async areAsrModelsReady(): Promise<boolean> {
    return ScreenCaptureBridge.areAsrModelsReady();
  }

  async downloadAsrModels(): Promise<void> {
    return ScreenCaptureBridge.downloadAsrModels();
  }

  onAsrModelDownloadProgress(cb: (downloaded: number, total: number) => void): () => void {
    return ScreenCaptureBridge.onAsrModelDownloadProgress(cb);
  }
}

// ─── Singleton ────────────────────────────────────────────────────────────────

const instance = new ScreenCaptureLiveTranslation();

/** Get the live translation controller. Returns the same instance every call. */
export function getLiveTranslation(): LiveTranslation {
  return instance;
}
