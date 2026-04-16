import { NativeModules, Platform, DeviceEventEmitter, EmitterSubscription } from "react-native";

const { ScreenCaptureModule } = NativeModules;

// ─── Types ────────────────────────────────────────────────────────────────────

export interface LiveSubtitleEvent {
  original: string;
  translated: string;
}

export interface CaptionSegment {
  start: number;
  end: number;
  text: string;
  /** Relative horizontal position 0–1. null = speech caption (subtitle bar). */
  x: number | null;
  /** Relative vertical position 0–1. null = speech caption (subtitle bar). */
  y: number | null;
}

export interface CaptionsResponse {
  segments: CaptionSegment[];
  count: number;
}

// ─── Guards ───────────────────────────────────────────────────────────────────

function assertAndroid(): void {
  if (Platform.OS !== "android") {
    throw new Error("Screen capture translation is only available on Android.");
  }
}

function assertModule(): void {
  if (!ScreenCaptureModule) {
    throw new Error(
      "ScreenCaptureModule not found. Ensure you are running a custom dev client build."
    );
  }
}

// ─── Public API ───────────────────────────────────────────────────────────────

export const ScreenCaptureBridge = {
  isAvailable(): boolean {
    return Platform.OS === "android" && !!ScreenCaptureModule;
  },

  // ─── Permissions ────────────────────────────────────────────────────────────

  async requestPermission(): Promise<boolean> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.requestPermission();
  },

  async checkOverlayPermission(): Promise<boolean> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.checkOverlayPermission();
  },

  requestOverlayPermission(): void {
    assertAndroid(); assertModule();
    ScreenCaptureModule.requestOverlayPermission();
  },

  // ─── Session ─────────────────────────────────────────────────────────────────

  /**
   * Start live translation.
   *
   * @param targetLang  BCP-47 target language code ("zh", "de", "en", …)
   * @param sourceLang  BCP-47 code of the video subtitle language — selects OCR script
   *                    ("zh" → Chinese, "ja" → Japanese, "auto" → Latin)
   * @param enableAudio Also run the audio ASR path alongside video OCR
   * @param apiKey      Anthropic API key for cloud streaming translation
   */
  async start(targetLang: string, sourceLang: string, enableAudio: boolean, apiKey: string): Promise<void> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.start(targetLang, sourceLang, enableAudio, apiKey);
  },

  async stop(): Promise<void> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.stop();
  },

  async isRunning(): Promise<boolean> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.isRunning();
  },

  // ─── ASR model management ────────────────────────────────────────────────────

  async areAsrModelsReady(): Promise<boolean> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.areAsrModelsReady();
  },

  async downloadAsrModels(): Promise<void> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.downloadAsrModels();
  },

  onAsrModelDownloadProgress(
    callback: (downloaded: number, total: number) => void
  ): () => void {
    if (Platform.OS !== "android") return () => {};
    const sub: EmitterSubscription = DeviceEventEmitter.addListener(
      "onAsrModelDownloadProgress",
      ({ downloaded, total }: { downloaded: number; total: number }) =>
        callback(downloaded, total)
    );
    return () => sub.remove();
  },

  // ─── Share intent ────────────────────────────────────────────────────────────

  /**
   * Returns the URL received from the last Android share-sheet intent, then clears it.
   * Returns null if no share intent has arrived since the last call.
   */
  async getSharedUrl(): Promise<string | null> {
    if (Platform.OS !== "android" || !ScreenCaptureModule) return null;
    return ScreenCaptureModule.getSharedUrl();
  },

  // ─── Caption sync (platform API) ─────────────────────────────────────────────

  /**
   * Fetch pre-timed captions from the backend for a TikTok / YouTube / Instagram URL.
   * Returns the raw segments in the source language — pass to [playCaptions] to translate.
   */
  async loadCaptions(url: string, apiBaseUrl: string, authToken?: string): Promise<CaptionsResponse> {
    const headers: Record<string, string> = { "Content-Type": "application/json" };
    if (authToken) headers["Authorization"] = `Bearer ${authToken}`;
    const res = await fetch(`${apiBaseUrl}/api/v1/captions?url=${encodeURIComponent(url)}`, { headers });
    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error((body as any).detail ?? `HTTP ${res.status}`);
    }
    return res.json();
  },

  /**
   * Translate all segments on-device with ML Kit and start the timer-based overlay.
   * Resolves with the number of translated segments once the overlay is running.
   *
   * Listen to [onCaptionTranslateProgress] for per-segment progress while this awaits.
   */
  async playCaptions(segments: CaptionSegment[], targetLang: string): Promise<number> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.playCaptions(segments, targetLang);
  },

  /** Stop the caption sync overlay. */
  async stopCaptions(): Promise<void> {
    assertAndroid(); assertModule();
    return ScreenCaptureModule.stopCaptions();
  },

  /** Subscribe to per-segment translation progress while [playCaptions] is running. */
  onCaptionTranslateProgress(
    callback: (done: number, total: number) => void
  ): () => void {
    if (Platform.OS !== "android") return () => {};
    const sub: EmitterSubscription = DeviceEventEmitter.addListener(
      "onCaptionTranslateProgress",
      ({ done, total }: { done: number; total: number }) => callback(done, total)
    );
    return () => sub.remove();
  },

  // ─── Subtitle events ─────────────────────────────────────────────────────────

  onSubtitle(callback: (event: LiveSubtitleEvent) => void): () => void {
    if (Platform.OS !== "android") return () => {};
    const sub: EmitterSubscription = DeviceEventEmitter.addListener(
      "onLiveSubtitle",
      (e: LiveSubtitleEvent) => callback(e)
    );
    return () => sub.remove();
  },
} as const;
