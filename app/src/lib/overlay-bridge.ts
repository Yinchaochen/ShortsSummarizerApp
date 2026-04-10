import { NativeModules, Platform } from "react-native";

const { BubbleModule } = NativeModules;

// ─── Types ────────────────────────────────────────────────────────────────────

export interface BubblePermissionStatus {
  /** Whether the AccessibilityService is enabled (needed for subtitle reading). */
  hasAccessibilityPermission: boolean;
  /** Whether the device runs Android 11+ and supports Bubbles. */
  supported: boolean;
}

export interface OverlayConfig {
  targetLanguage: string;
  opacity: number;       // 0.0 – 1.0
  textSize: number;      // sp
  engine: "mlkit" | "cloud"; // Phase 3+: swap translation engine here
}

// ─── Guards ───────────────────────────────────────────────────────────────────

function assertAndroid(): void {
  if (Platform.OS !== "android") {
    throw new Error("Bubble feature is only available on Android.");
  }
}

function assertModule(): void {
  if (!BubbleModule) {
    throw new Error(
      "BubbleModule not found. Ensure you are running a native build (not Expo Go)."
    );
  }
}

// ─── Public API ───────────────────────────────────────────────────────────────

export const OverlayBridge = {
  /** Phase 1: verify native bridge is connected. */
  ping(): Promise<string> {
    assertAndroid();
    assertModule();
    return BubbleModule.ping();
  },

  /**
   * Returns true if the device supports Bubbles (Android 11+).
   * Safe to call on iOS — always resolves false.
   */
  isSupported(): Promise<boolean> {
    if (Platform.OS !== "android") return Promise.resolve(false);
    if (!BubbleModule) return Promise.resolve(false);
    return BubbleModule.isSupported();
  },

  /** Show (or update) the floating translation bubble. */
  showTranslationBubble(text: string): Promise<void> {
    assertAndroid();
    assertModule();
    return BubbleModule.showTranslationBubble(text);
  },

  /**
   * Show a pet bubble identified by petId.
   * Returns the notification ID so it can be dismissed later.
   */
  showPetBubble(petId: string, petName: string): Promise<number> {
    assertAndroid();
    assertModule();
    return BubbleModule.showPetBubble(petId, petName);
  },

  /** Dismiss a bubble by its notification ID. */
  dismissBubble(notifId: number): Promise<void> {
    assertAndroid();
    assertModule();
    return BubbleModule.dismissBubble(notifId);
  },

  /** Check AccessibilityService status + device support. */
  checkPermissions(): Promise<BubblePermissionStatus> {
    assertAndroid();
    assertModule();
    return BubbleModule.checkPermissions();
  },

  /** Open Android Accessibility Settings so user can enable the service. */
  requestAccessibilityPermission(): void {
    assertAndroid();
    assertModule();
    BubbleModule.requestAccessibilityPermission();
  },

  /** Phase 2+: start the translation service with the given config. */
  start(config: OverlayConfig): Promise<void> {
    assertAndroid();
    assertModule();
    return BubbleModule.start(config);
  },

  /** Phase 2+: stop the translation service. */
  stop(): Promise<void> {
    assertAndroid();
    assertModule();
    return BubbleModule.stop();
  },

  /** Phase 2+: update config on the running service without restarting. */
  updateConfig(config: Partial<OverlayConfig>): Promise<void> {
    assertAndroid();
    assertModule();
    return BubbleModule.updateConfig(config);
  },
} as const;
