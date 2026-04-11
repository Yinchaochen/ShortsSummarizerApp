import { useState, useEffect, useCallback } from "react";
import {
  View, Text, TouchableOpacity, StyleSheet,
  ActivityIndicator, Platform, Switch, ScrollView, Alert,
} from "react-native";
import { router } from "expo-router";
import BreathingBackground from "../src/shared/components/BreathingBackground";
import { OverlayBridge, BubblePermissionStatus } from "../src/features/live-translation/overlay-bridge";
import {
  getAvailableStrategies,
  LiveTranslationStrategy,
  SubtitleEvent,
} from "../src/features/live-translation";
import LanguagePicker from "../src/shared/components/LanguagePicker";
import { useLanguage } from "../src/shared/context/LanguageContext";

type BridgeState = "idle" | "loading" | "ok" | "error";

export default function OverlaySettingsScreen() {
  const { t, langCode } = useLanguage();

  // Bridge debug
  const [bridgeState, setBridgeState] = useState<BridgeState>("idle");
  const [bridgeMsg, setBridgeMsg] = useState("");

  // Permissions
  const [permissions, setPermissions] = useState<BubblePermissionStatus | null>(null);
  const [permLoading, setPermLoading] = useState(false);

  // Live Translation (strategy-based)
  const [strategies] = useState(() => getAvailableStrategies());
  const [activeStrategy, setActiveStrategy] = useState<LiveTranslationStrategy | null>(
    () => strategies[0] ?? null
  );
  const [running, setRunning] = useState(false);
  const [runLoading, setRunLoading] = useState(false);
  const [subtitle, setSubtitle] = useState<SubtitleEvent | null>(null);
  const [targetLang, setTargetLang] = useState(langCode);

  // ── Bridge test ───────────────────────────────────────────────────────────
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

  // ── Permissions ───────────────────────────────────────────────────────────
  const refreshPermissions = useCallback(async () => {
    if (Platform.OS !== "android") return;
    setPermLoading(true);
    try {
      const status = await OverlayBridge.checkPermissions();
      setPermissions(status);
    } finally {
      setPermLoading(false);
    }
  }, []);

  // ── Live Translation toggle ───────────────────────────────────────────────
  const handleToggle = useCallback(async () => {
    if (!activeStrategy) return;

    if (running) {
      setRunLoading(true);
      await activeStrategy.stop().catch(() => {});
      setRunning(false);
      setSubtitle(null);
      setRunLoading(false);
      return;
    }

    setRunLoading(true);
    try {
      const granted = await activeStrategy.requestPermissions();
      if (!granted) {
        if (activeStrategy.id === "screen-capture") {
          Alert.alert("Permission Required", "Grant the required permissions, then tap Start again.");
        } else {
          Alert.alert("Enable Accessibility Service", "Enable Uchia in Accessibility Settings, then tap Start again.");
        }
        return;
      }
      await activeStrategy.start(targetLang);
      setRunning(true);
    } catch (e: any) {
      Alert.alert("Error", e.message);
    } finally {
      setRunLoading(false);
    }
  }, [activeStrategy, running, targetLang]);

  // ── Subtitle subscription — re-subscribe when strategy changes ────────────
  useEffect(() => {
    if (!activeStrategy) return;
    const unsub = activeStrategy.onSubtitle(setSubtitle);
    return unsub;
  }, [activeStrategy]);

  useEffect(() => {
    if (Platform.OS === "android") refreshPermissions();
  }, []);

  const stateColor = { idle: "#62666d", loading: "#7170ff", ok: "#4caf50", error: "#ff6b6b" };

  return (
    <ScrollView style={styles.root} contentContainerStyle={styles.content}>
      <BreathingBackground />

      <TouchableOpacity style={styles.back} onPress={() => router.back()}>
        <Text style={styles.backText}>{t.back}</Text>
      </TouchableOpacity>
      <Text style={styles.title}>{t.overlayTitle}</Text>
      <Text style={styles.subtitle}>Bubbles · Phase 1</Text>

      {/* ── iOS placeholder ───────────────────────────────────────────── */}
      {Platform.OS !== "android" && (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Picture-in-Picture</Text>
          <Text style={styles.cardDesc}>
            On iOS, floating bubbles will be delivered via Picture-in-Picture.
            Full PiP support is coming in a future update.
          </Text>
        </View>
      )}

      {/* ── Android content ───────────────────────────────────────────── */}
      {Platform.OS === "android" && (
        <>
          {/* Native Bridge test */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>{t.overlayBridgeTitle}</Text>
            <Text style={styles.cardDesc}>{t.overlayBridgeDesc}</Text>
            <View style={styles.statusRow}>
              <View style={[styles.dot, { backgroundColor: stateColor[bridgeState] }]} />
              <Text style={[styles.statusText, { color: stateColor[bridgeState] }]}>
                {bridgeState === "idle" && t.overlayBridgeIdle}
                {bridgeState === "loading" && t.overlayBridgePinging}
                {(bridgeState === "ok" || bridgeState === "error") && bridgeMsg}
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

          {/* Live Translation */}
          {strategies.length > 0 && (
            <View style={styles.card}>
              <Text style={styles.cardTitle}>{t.overlayTitle}</Text>

              {/* Strategy selector — only visible when multiple strategies available */}
              {strategies.length > 1 && (
                <View style={styles.strategyRow}>
                  {strategies.map((s) => (
                    <TouchableOpacity
                      key={s.id}
                      style={[styles.strategyChip, activeStrategy?.id === s.id && styles.strategyChipActive]}
                      onPress={() => {
                        if (running) return; // prevent switching while active
                        setActiveStrategy(s);
                        setSubtitle(null);
                      }}
                    >
                      <Text style={[styles.strategyChipText, activeStrategy?.id === s.id && styles.strategyChipTextActive]}>
                        {s.label}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}

              <Text style={styles.label}>Translate to</Text>
              <LanguagePicker
                value={targetLang}
                onChange={(lang) => setTargetLang(lang.code)}
                searchPlaceholder={t.searchLanguage}
              />

              <Text style={styles.cardDesc}>
                {activeStrategy?.id === "screen-capture"
                  ? "Captures your screen and uses OCR to detect subtitles. No AccessibilityService needed — works in any app."
                  : "Reads subtitles via Accessibility APIs. First use downloads a ~30 MB on-device translation model."}
              </Text>

              <TouchableOpacity
                style={[
                  styles.button,
                  running && { backgroundColor: "#ff6b6b" },
                  runLoading && styles.buttonDisabled,
                ]}
                onPress={handleToggle}
                disabled={runLoading}
              >
                {runLoading
                  ? <ActivityIndicator color="#08090a" size="small" />
                  : <Text style={styles.buttonText}>{running ? "Stop" : "Start Live Translation"}</Text>
                }
              </TouchableOpacity>

              {subtitle && (
                <View style={styles.subtitleBox}>
                  <Text style={styles.subtitleSource}>
                    {activeStrategy?.id === "screen-capture" ? "OCR" : "Accessibility"}
                  </Text>
                  {subtitle.original !== subtitle.translated && (
                    <Text style={styles.subtitleOriginal}>{subtitle.original}</Text>
                  )}
                  <Text style={styles.subtitleTranslated}>{subtitle.translated}</Text>
                </View>
              )}
            </View>
          )}

          {/* Permissions */}
          <View style={styles.card}>
            <Text style={styles.cardTitle}>{t.overlayPermissionsTitle}</Text>
            {permLoading
              ? <ActivityIndicator color="#7170ff" style={{ marginVertical: 8 }} />
              : permissions && (
                <PermissionRow
                  label={t.overlayAccessibilityPerm}
                  grantLabel={t.overlayGrant}
                  granted={permissions.hasAccessibilityPermission}
                  onRequest={() => {
                    OverlayBridge.requestAccessibilityPermission();
                    setTimeout(refreshPermissions, 3000);
                  }}
                />
              )
            }
            <TouchableOpacity style={styles.secondaryButton} onPress={refreshPermissions}>
              <Text style={styles.secondaryButtonText}>{t.overlayRefresh}</Text>
            </TouchableOpacity>
          </View>
        </>
      )}
    </ScrollView>
  );
}

function PermissionRow({ label, grantLabel, granted, onRequest }: {
  label: string; grantLabel: string; granted: boolean; onRequest: () => void;
}) {
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
  strategyRow: { flexDirection: "row", gap: 8 },
  strategyChip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.1)",
  },
  strategyChipActive: { borderColor: "#7170ff", backgroundColor: "rgba(113,112,255,0.12)" },
  strategyChipText: { color: "#62666d", fontSize: 12 },
  strategyChipTextActive: { color: "#7170ff" },
  label: { color: "#d0d6e0", fontSize: 13, fontWeight: "500" },
  subtitleBox: { gap: 4, paddingTop: 4 },
  subtitleSource: { color: "#7170ff", fontSize: 11, textTransform: "uppercase", letterSpacing: 0.5 },
  subtitleOriginal: { color: "#62666d", fontSize: 12 },
  subtitleTranslated: { color: "#f7f8f8", fontSize: 15, lineHeight: 22 },
  permRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingVertical: 4 },
  permLeft: { flexDirection: "row", alignItems: "center", gap: 8 },
  permLabel: { color: "#d0d6e0", fontSize: 14 },
  grantText: { color: "#7170ff", fontSize: 13 },
});
