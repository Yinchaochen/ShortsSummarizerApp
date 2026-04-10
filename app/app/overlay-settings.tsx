import { useState, useEffect, useCallback } from "react";
import {
  View, Text, TouchableOpacity, StyleSheet,
  ActivityIndicator, Platform, Switch, ScrollView, Alert,
} from "react-native";
import { router } from "expo-router";
import BreathingBackground from "../src/components/BreathingBackground";
import { OverlayBridge, BubblePermissionStatus } from "../src/lib/overlay-bridge";
import { ScreenCaptureBridge, LiveSubtitleEvent } from "../src/lib/screen-capture-bridge";
import LanguagePicker from "../src/components/LanguagePicker";
import { useLanguage } from "./_layout";

type BridgeState = "idle" | "loading" | "ok" | "error";
type BubbleTestState = "idle" | "loading" | "ok" | "error";

export default function OverlaySettingsScreen() {
  const { t, langCode } = useLanguage();
  const [bridgeState, setBridgeState] = useState<BridgeState>("idle");
  const [bridgeMsg, setBridgeMsg] = useState("");
  const [permissions, setPermissions] = useState<BubblePermissionStatus | null>(null);
  const [permLoading, setPermLoading] = useState(false);
  const [bubbleTest, setBubbleTest] = useState<BubbleTestState>("idle");
  const [liveSubtitle, setLiveSubtitle] = useState<string | null>(null);
  const [subtitleSource, setSubtitleSource] = useState("");
  const [overlayEnabled, setOverlayEnabled] = useState(true);
  const [targetLang, setTargetLang] = useState(langCode);

  // ── Screen Capture (Method 2) state ─────────────────────────────
  const [captureAvailable] = useState(() => ScreenCaptureBridge.isAvailable());
  const [captureRunning, setCaptureRunning] = useState(false);
  const [captureSubtitle, setCaptureSubtitle] = useState<LiveSubtitleEvent | null>(null);
  const [captureLoading, setCaptureLoading] = useState(false);

  const testBridge = useCallback(async () => {
    setBridgeState("loading");
    setBridgeMsg("");
    try {
      const res = await OverlayBridge.ping();
      setBridgeState("ok");
      setBridgeMsg(`${res}`);
    } catch (e: any) {
      setBridgeState("error");
      setBridgeMsg(e.message ?? "Unknown error");
    }
  }, []);

  const refreshPermissions = useCallback(async () => {
    setPermLoading(true);
    try {
      const status = await OverlayBridge.checkPermissions();
      setPermissions(status);
    } catch (e: any) {
      setBridgeMsg(e.message ?? t.failedToConnect);
    } finally {
      setPermLoading(false);
    }
  }, [t]);

  const handleOverlayToggle = useCallback(async (val: boolean) => {
    setOverlayEnabled(val);
    if (Platform.OS === "android") {
      await OverlayBridge.setOverlayEnabled(val).catch(() => {});
    }
  }, []);

  const handleTargetLangChange = useCallback(async (lang: { code: string }) => {
    setTargetLang(lang.code);
    if (Platform.OS === "android") {
      await OverlayBridge.setTargetLanguage(lang.code).catch(() => {});
    }
  }, []);

  const testBubble = useCallback(async () => {
    setBubbleTest("loading");
    try {
      await OverlayBridge.showTranslationBubble("Hello from Uchia! 👋 This is a test bubble.");
      setBubbleTest("ok");
    } catch (e: any) {
      setBubbleTest("error");
    }
  }, []);

  const handleCaptureToggle = useCallback(async () => {
    if (captureRunning) {
      setCaptureLoading(true);
      await ScreenCaptureBridge.stop();
      setCaptureRunning(false);
      setCaptureSubtitle(null);
      setCaptureLoading(false);
      return;
    }
    setCaptureLoading(true);
    try {
      // 1. Check "Display over other apps" permission (needed for positional bubbles)
      const overlayGranted = await ScreenCaptureBridge.checkOverlayPermission();
      if (!overlayGranted) {
        Alert.alert(
          "Permission Required",
          "\"Display over other apps\" must be enabled so Uchia can show translation bubbles. Tap OK to open settings.",
          [
            { text: "Cancel", style: "cancel" },
            {
              text: "Open Settings",
              onPress: () => ScreenCaptureBridge.requestOverlayPermission(),
            },
          ]
        );
        setCaptureLoading(false);
        return;
      }

      // 2. Request screen recording permission (MediaProjection dialog)
      const granted = await ScreenCaptureBridge.requestPermission();
      if (!granted) {
        Alert.alert("Permission Denied", "Screen capture permission is required for live translation.");
        setCaptureLoading(false);
        return;
      }

      // 3. Start capture
      const ocrScript = ScreenCaptureBridge.ocrScriptForLanguage(targetLang);
      await ScreenCaptureBridge.start(targetLang, ocrScript);
      setCaptureRunning(true);
    } catch (e: any) {
      Alert.alert("Error", e.message);
    } finally {
      setCaptureLoading(false);
    }
  }, [captureRunning, targetLang]);

  useEffect(() => {
    if (Platform.OS === "android") {
      refreshPermissions();
    }
    const unsub = OverlayBridge.onSubtitleDetected((text, source) => {
      setLiveSubtitle(text);
      setSubtitleSource(source.split(".").pop() ?? source);
    });
    const unsubCapture = ScreenCaptureBridge.onSubtitle((event) => {
      setCaptureSubtitle(event);
    });
    return () => { unsub(); unsubCapture(); };
  }, []);

  const stateColor = { idle: "#62666d", loading: "#7170ff", ok: "#4caf50", error: "#ff6b6b" };
  const bubbleColor = { idle: "#62666d", loading: "#7170ff", ok: "#4caf50", error: "#ff6b6b" };

  return (
    <ScrollView style={styles.root} contentContainerStyle={styles.content}>
      <BreathingBackground />

      <TouchableOpacity style={styles.back} onPress={() => router.back()}>
        <Text style={styles.backText}>{t.back}</Text>
      </TouchableOpacity>
      <Text style={styles.title}>{t.overlayTitle}</Text>
      <Text style={styles.subtitle}>Bubbles · Phase 1</Text>

      {/* ── iOS placeholder ─────────────────────────────── */}
      {Platform.OS !== "android" && (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Picture-in-Picture</Text>
          <Text style={styles.cardDesc}>
            On iOS, floating bubbles are delivered via Picture-in-Picture mode.
            Full PiP support is coming in a future update.
          </Text>
        </View>
      )}

      {/* ── Android content ─────────────────────────────── */}
      {Platform.OS === "android" && (
        <>
          {/* Bridge test */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>{t.overlayBridgeTitle}</Text>
            <Text style={styles.cardDesc}>{t.overlayBridgeDesc}</Text>

            <View style={styles.statusRow}>
              <View style={[styles.dot, { backgroundColor: stateColor[bridgeState] }]} />
              <Text style={[styles.statusText, { color: stateColor[bridgeState] }]}>
                {bridgeState === "idle" && t.overlayBridgeIdle}
                {bridgeState === "loading" && t.overlayBridgePinging}
                {bridgeState === "ok" && bridgeMsg}
                {bridgeState === "error" && bridgeMsg}
              </Text>
            </View>

            <TouchableOpacity
              style={[styles.button, bridgeState === "loading" && styles.buttonDisabled]}
              onPress={testBridge}
              disabled={bridgeState === "loading"}
            >
              {bridgeState === "loading"
                ? <ActivityIndicator color="#08090a" size="small" />
                : <Text style={styles.buttonText}>{t.overlayTestButton}</Text>
              }
            </TouchableOpacity>
          </View>

          {/* Bubble test */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>Bubble Test</Text>
            <Text style={styles.cardDesc}>
              Posts a floating bubble on screen. Requires Android 11+ and notification permission.
            </Text>

            <View style={styles.statusRow}>
              <View style={[styles.dot, { backgroundColor: bubbleColor[bubbleTest] }]} />
              <Text style={[styles.statusText, { color: bubbleColor[bubbleTest] }]}>
                {bubbleTest === "idle" && "Not tested yet"}
                {bubbleTest === "loading" && "Posting bubble…"}
                {bubbleTest === "ok" && "Bubble posted — check your screen"}
                {bubbleTest === "error" && "Failed (Android 11+ required)"}
              </Text>
            </View>

            <TouchableOpacity
              style={[styles.button, bubbleTest === "loading" && styles.buttonDisabled]}
              onPress={testBubble}
              disabled={bubbleTest === "loading"}
            >
              {bubbleTest === "loading"
                ? <ActivityIndicator color="#08090a" size="small" />
                : <Text style={styles.buttonText}>Show bubble →</Text>
              }
            </TouchableOpacity>
          </View>

          {/* Translation settings */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>Live Translation</Text>

            <View style={styles.toggleRow}>
              <Text style={styles.toggleLabel}>Enable overlay</Text>
              <Switch
                value={overlayEnabled}
                onValueChange={handleOverlayToggle}
                trackColor={{ false: "rgba(255,255,255,0.1)", true: "#7170ff" }}
                thumbColor="#f7f8f8"
              />
            </View>

            <Text style={styles.label}>Translate to</Text>
            <LanguagePicker
              value={targetLang}
              onChange={handleTargetLangChange}
              searchPlaceholder={t.searchLanguage}
            />

            <Text style={styles.cardDesc}>
              First use downloads a ~30 MB language model. Translation runs fully on-device — no internet needed.
            </Text>
          </View>

          {/* Permissions */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>{t.overlayPermissionsTitle}</Text>

            {permLoading
              ? <ActivityIndicator color="#7170ff" style={{ marginVertical: 8 }} />
              : permissions && (
                <>
                  <PermissionRow
                    label={t.overlayAccessibilityPerm}
                    grantLabel={t.overlayGrant}
                    granted={permissions.hasAccessibilityPermission}
                    onRequest={() => {
                      OverlayBridge.requestAccessibilityPermission();
                      setTimeout(refreshPermissions, 3000);
                    }}
                  />
                  {!permissions.supported && (
                    <Text style={styles.warningText}>
                      Bubbles require Android 11 or later.
                    </Text>
                  )}
                </>
              )
            }

            <TouchableOpacity style={styles.secondaryButton} onPress={refreshPermissions}>
              <Text style={styles.secondaryButtonText}>{t.overlayRefresh}</Text>
            </TouchableOpacity>
          </View>
          {/* ── Screen Capture Mode (Method 2) ─────────────────── */}
          {captureAvailable && (
            <View style={styles.card}>
              <Text style={styles.cardTitle}>🎬 Screen Capture Translation</Text>
              <Text style={styles.cardDesc}>
                Captures your screen and uses OCR to detect subtitles in any app.
                No AccessibilityService needed — works everywhere.
              </Text>

              <TouchableOpacity
                style={[
                  styles.button,
                  captureRunning && { backgroundColor: "#ff6b6b" },
                  captureLoading && styles.buttonDisabled,
                ]}
                onPress={handleCaptureToggle}
                disabled={captureLoading}
              >
                {captureLoading
                  ? <ActivityIndicator color="#08090a" size="small" />
                  : <Text style={styles.buttonText}>
                      {captureRunning ? "Stop Capture" : "Start Live Translation"}
                    </Text>
                }
              </TouchableOpacity>

              {captureSubtitle && (
                <View style={{ gap: 4, marginTop: 8 }}>
                  <Text style={styles.subtitleSource}>OCR Detected</Text>
                  <Text style={{ color: "#62666d", fontSize: 12 }}>{captureSubtitle.original}</Text>
                  <Text style={styles.subtitleText}>{captureSubtitle.translated}</Text>
                </View>
              )}
            </View>
          )}

          {/* Live subtitle monitor (Method 1: Accessibility) */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>Live Subtitle Monitor (Accessibility)</Text>
            <Text style={styles.cardDesc}>
              Open TikTok, YouTube or Instagram Reels — detected subtitles appear here in real time.
            </Text>
            {liveSubtitle ? (
              <>
                <Text style={styles.subtitleSource}>{subtitleSource}</Text>
                <Text style={styles.subtitleText}>{liveSubtitle}</Text>
              </>
            ) : (
              <Text style={styles.cardDesc}>Waiting for subtitles…</Text>
            )}
          </View>
        </>
      )}
    </ScrollView>
  );
}

function PermissionRow({
  label, grantLabel, granted, onRequest,
}: { label: string; grantLabel: string; granted: boolean; onRequest: () => void }) {
  return (
    <View style={styles.permRow}>
      <View style={styles.permLeft}>
        <View style={[styles.dot, { backgroundColor: granted ? "#4caf50" : "#ff6b6b" }]} />
        <Text style={styles.permLabel}>{label}</Text>
      </View>
      {!granted && (
        <TouchableOpacity onPress={onRequest}>
          <Text style={styles.grantText}>{grantLabel}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: "#08090a" },
  content: { paddingHorizontal: 24, paddingTop: 64, paddingBottom: 48 },
  back: { marginBottom: 24 },
  backText: { color: "#7170ff", fontSize: 15 },
  title: { fontSize: 32, fontWeight: "600", color: "#f7f8f8", letterSpacing: -1, marginBottom: 4 },
  subtitle: { fontSize: 14, color: "#62666d", marginBottom: 32 },
  card: {
    backgroundColor: "rgba(255,255,255,0.03)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    padding: 20,
    gap: 12,
    marginBottom: 16,
  },
  cardTitle: { color: "#f7f8f8", fontWeight: "600", fontSize: 15 },
  cardDesc: { color: "#62666d", fontSize: 13, lineHeight: 20 },
  statusRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  dot: { width: 8, height: 8, borderRadius: 4 },
  statusText: { fontSize: 13, flex: 1 },
  button: {
    backgroundColor: "#7170ff",
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: "center",
  },
  buttonDisabled: { opacity: 0.5 },
  buttonText: { color: "#08090a", fontWeight: "600", fontSize: 14 },
  secondaryButton: {
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    borderRadius: 8,
    paddingVertical: 10,
    alignItems: "center",
  },
  secondaryButtonText: { color: "#62666d", fontSize: 13 },
  permRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 4,
  },
  permLeft: { flexDirection: "row", alignItems: "center", gap: 8 },
  permLabel: { color: "#d0d6e0", fontSize: 14 },
  grantText: { color: "#7170ff", fontSize: 13 },
  warningText: { color: "#ff6b6b", fontSize: 12, marginTop: 4 },
  subtitleSource: { color: "#7170ff", fontSize: 11, letterSpacing: 0.1, textTransform: "uppercase" },
  subtitleText: { color: "#f7f8f8", fontSize: 15, lineHeight: 22 },
  toggleRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  toggleLabel: { color: "#d0d6e0", fontSize: 14 },
  label: { color: "#d0d6e0", fontSize: 13, fontWeight: "500" },
});
