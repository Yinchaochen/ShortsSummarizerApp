/**
 * Unified Live Translation interface.
 *
 * Strategy pattern: each method encapsulates one way of capturing and
 * translating subtitles (Accessibility Service, Screen Capture, …).
 * All strategies implement the same interface, so callers (overlay-settings.tsx,
 * future bubble UI, iOS PiP, etc.) don't need to know which method is active.
 *
 * To add a new platform/method:
 *   1. Create a class that implements LiveTranslationStrategy.
 *   2. Add an instance to the `strategies` array below.
 *   That's it — getAvailableStrategies() will pick it up automatically.
 */

import { Platform, Alert } from "react-native";
import { OverlayBridge } from "./overlay-bridge";
import { ScreenCaptureBridge, LiveSubtitleEvent } from "./screen-capture-bridge";

// ─── Shared types ─────────────────────────────────────────────────────────────

export type SubtitleEvent = {
  original: string;
  translated: string;
};

export interface LiveTranslationStrategy {
  /** Stable machine-readable identifier. */
  readonly id: "accessibility" | "screen-capture";
  /** Human-readable label for UI. */
  readonly label: string;
  /** Returns true if this strategy can run on the current device/OS. */
  isAvailable(): boolean;
  /** Returns true if all required permissions are already granted. */
  checkPermissions(): Promise<boolean>;
  /**
   * Request permissions. Returns true if all permissions were granted.
   * May show system dialogs or open settings — call only in response to a user tap.
   */
  requestPermissions(): Promise<boolean>;
  /** Start capturing and translating. Permissions must be granted first. */
  start(targetLang: string): Promise<void>;
  /** Stop the active capture session. */
  stop(): Promise<void>;
  /** Subscribe to subtitle events. Returns an unsubscribe function. */
  onSubtitle(callback: (event: SubtitleEvent) => void): () => void;
}

// ─── Accessibility Service strategy ──────────────────────────────────────────

class AccessibilityServiceStrategy implements LiveTranslationStrategy {
  readonly id = "accessibility" as const;
  readonly label = "Accessibility Service";

  isAvailable(): boolean {
    return Platform.OS === "android";
  }

  async checkPermissions(): Promise<boolean> {
    try {
      const status = await OverlayBridge.checkPermissions();
      return status.hasAccessibilityPermission;
    } catch {
      return false;
    }
  }

  async requestPermissions(): Promise<boolean> {
    // If already granted, return true immediately so start() is called.
    const already = await this.checkPermissions();
    if (already) return true;
    // Not yet granted — open system settings for the user to enable it.
    OverlayBridge.requestAccessibilityPermission();
    return false;
  }

  async start(targetLang: string): Promise<void> {
    await OverlayBridge.setTargetLanguage(targetLang);
    await OverlayBridge.setOverlayEnabled(true);
  }

  async stop(): Promise<void> {
    await OverlayBridge.setOverlayEnabled(false);
  }

  onSubtitle(callback: (event: SubtitleEvent) => void): () => void {
    return OverlayBridge.onSubtitleDetected((text) =>
      callback({ original: text, translated: text })
    );
  }
}

// ─── Screen Capture strategy ──────────────────────────────────────────────────

class ScreenCaptureStrategy implements LiveTranslationStrategy {
  readonly id = "screen-capture" as const;
  readonly label = "Screen Capture";

  isAvailable(): boolean {
    return ScreenCaptureBridge.isAvailable();
  }

  async checkPermissions(): Promise<boolean> {
    return ScreenCaptureBridge.checkOverlayPermission();
  }

  async requestPermissions(): Promise<boolean> {
    const overlayGranted = await ScreenCaptureBridge.checkOverlayPermission();
    if (!overlayGranted) {
      ScreenCaptureBridge.requestOverlayPermission();
      return false;
    }
    return ScreenCaptureBridge.requestPermission();
  }

  async start(targetLang: string): Promise<void> {
    const ocrScript = ScreenCaptureBridge.ocrScriptForLanguage(targetLang);
    await ScreenCaptureBridge.start(targetLang, ocrScript);
  }

  async stop(): Promise<void> {
    await ScreenCaptureBridge.stop();
  }

  onSubtitle(callback: (event: SubtitleEvent) => void): () => void {
    return ScreenCaptureBridge.onSubtitle((e: LiveSubtitleEvent) =>
      callback({ original: e.original, translated: e.translated })
    );
  }
}

// ─── Registry ─────────────────────────────────────────────────────────────────

// Order matters: first available strategy is the default.
const ALL_STRATEGIES: LiveTranslationStrategy[] = [
  new ScreenCaptureStrategy(),       // Preferred: no Accessibility Service needed
  new AccessibilityServiceStrategy(), // Fallback
];

/** All strategies that can run on this device. */
export function getAvailableStrategies(): LiveTranslationStrategy[] {
  return ALL_STRATEGIES.filter((s) => s.isAvailable());
}

/** The best (first available) strategy, or null if none available. */
export function getBestStrategy(): LiveTranslationStrategy | null {
  return getAvailableStrategies()[0] ?? null;
}
