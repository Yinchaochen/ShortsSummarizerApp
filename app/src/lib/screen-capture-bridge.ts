import { NativeModules, Platform, DeviceEventEmitter, EmitterSubscription } from "react-native";

const { ScreenCaptureModule } = NativeModules;

// ─── Types ────────────────────────────────────────────────────────────────────

export type OcrScript = "latin" | "chinese" | "japanese" | "korean";

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
      "ScreenCaptureModule not found. Ensure you are running a custom dev client build (not Expo Go)."
    );
  }
}

// ─── Public API ───────────────────────────────────────────────────────────────

export const ScreenCaptureBridge = {
  /**
   * Check if this feature is available on the current platform.
   * Returns true on Android with custom dev client, false everywhere else.
   */
  isAvailable(): boolean {
    return Platform.OS === "android" && !!ScreenCaptureModule;
  },

  /**
   * Request screen capture permission from the user.
   * Shows Android's "Start recording?" system dialog.
   * @returns true if permission granted, false if denied.
   */
  async requestPermission(): Promise<boolean> {
    assertAndroid();
    assertModule();
    return ScreenCaptureModule.requestPermission();
  },

  /**
   * Start live screen capture + OCR + translation.
   * Must call requestPermission() first.
   *
   * @param targetLang - BCP 47 language code (e.g. "zh", "en", "de")
   * @param ocrScript  - Which OCR model to use for text recognition
   */
  async start(targetLang: string, ocrScript: OcrScript = "latin"): Promise<void> {
    assertAndroid();
    assertModule();
    return ScreenCaptureModule.start(targetLang, ocrScript);
  },

  /**
   * Stop the screen capture service.
   */
  async stop(): Promise<void> {
    assertAndroid();
    assertModule();
    return ScreenCaptureModule.stop();
  },

  /**
   * Check if capture is currently running.
   */
  async isRunning(): Promise<boolean> {
    assertAndroid();
    assertModule();
    return ScreenCaptureModule.isRunning();
  },

  /**
   * Check whether the "Display over other apps" (SYSTEM_ALERT_WINDOW) permission
   * is granted. Required before calling start().
   * @returns true if granted, false if not.
   */
  async checkOverlayPermission(): Promise<boolean> {
    assertAndroid();
    assertModule();
    return ScreenCaptureModule.checkOverlayPermission();
  },

  /**
   * Open the system "Display over other apps" settings page for this app.
   * Call this when checkOverlayPermission() returns false.
   */
  requestOverlayPermission(): void {
    assertAndroid();
    assertModule();
    ScreenCaptureModule.requestOverlayPermission();
  },

  /**
   * Subscribe to live subtitle events.
   * Fires each time a new subtitle is detected and translated.
   * Returns an unsubscribe function — call it in useEffect cleanup.
   */
  onSubtitle(callback: (event: LiveSubtitleEvent) => void): () => void {
    if (Platform.OS !== "android") return () => {};
    const sub: EmitterSubscription = DeviceEventEmitter.addListener(
      "onLiveSubtitle",
      (e: LiveSubtitleEvent) => callback(e)
    );
    return () => sub.remove();
  },

  /**
   * Helper: pick the right OCR script based on a BCP 47 language code.
   * Used to match the *source* video language for better recognition.
   */
  ocrScriptForLanguage(langCode: string): OcrScript {
    const code = langCode.toLowerCase().split("-")[0];
    switch (code) {
      case "zh": return "chinese";
      case "ja": return "japanese";
      case "ko": return "korean";
      default:   return "latin";
    }
  },
} as const;
