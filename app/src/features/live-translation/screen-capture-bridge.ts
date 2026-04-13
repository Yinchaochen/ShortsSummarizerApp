import { NativeModules, Platform, DeviceEventEmitter, EmitterSubscription } from "react-native";

const { ScreenCaptureModule } = NativeModules;

// ─── Types ────────────────────────────────────────────────────────────────────

export interface LiveSubtitleEvent {
  original: string;
  translated: string;
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
