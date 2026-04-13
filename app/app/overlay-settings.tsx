import { useState, useEffect, useCallback, useRef } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {
  View, Text, TouchableOpacity, StyleSheet,
  ActivityIndicator, Platform, Switch, ScrollView, Alert, TextInput,
} from "react-native";
import { router } from "expo-router";
import BreathingBackground from "../src/shared/components/BreathingBackground";
import { getLiveTranslation, LiveSubtitleEvent } from "../src/features/live-translation";
import { ScreenCaptureBridge } from "../src/features/live-translation/screen-capture-bridge";
import LanguagePicker from "../src/shared/components/LanguagePicker";
import { useLanguage } from "../src/shared/context/LanguageContext";

const lt = getLiveTranslation();

export default function OverlaySettingsScreen() {
  const { t, langCode } = useLanguage();

  // Session state
  const [running, setRunning]       = useState(false);
  const [runLoading, setRunLoading] = useState(false);
  const [subtitle, setSubtitle]     = useState<LiveSubtitleEvent | null>(null);
  const [targetLang, setTargetLang] = useState(langCode);

  // Pre-session config
  const [audioEnabled, setAudioEnabled] = useState(false);
  const [sourceLang, setSourceLang]     = useState("auto");
  const [apiKey, setApiKey]             = useState("");
  const [showApiKey, setShowApiKey]     = useState(false);
  const apiKeyLoaded = useRef(false);

  // ASR model download
  const [asrReady, setAsrReady]           = useState(false);
  const [asrDownloading, setAsrDownloading] = useState(false);
  const [asrProgress, setAsrProgress]     = useState<{ downloaded: number; total: number } | null>(null);

  // ── Persist API key ───────────────────────────────────────────────────────
  useEffect(() => {
    AsyncStorage.getItem("anthropicApiKey").then((val) => {
      if (val) setApiKey(val);
      apiKeyLoaded.current = true;
    });
  }, []);

  useEffect(() => {
    if (!apiKeyLoaded.current) return;
    AsyncStorage.setItem("anthropicApiKey", apiKey).catch(() => {});
  }, [apiKey]);

  // ── Init ──────────────────────────────────────────────────────────────────
  useEffect(() => {
    if (ScreenCaptureBridge.isAvailable()) {
      ScreenCaptureBridge.areAsrModelsReady().then(setAsrReady).catch(() => {});
    }
  }, []);

  // ── Subtitle subscription ─────────────────────────────────────────────────
  useEffect(() => {
    const unsub = lt.onSubtitle(setSubtitle);
    return unsub;
  }, []);

  // ── Session toggle ────────────────────────────────────────────────────────
  const handleToggle = useCallback(async () => {
    if (running) {
      setRunLoading(true);
      await lt.stop().catch(() => {});
      setRunning(false);
      setSubtitle(null);
      setRunLoading(false);
      return;
    }

    if (!apiKey.trim()) {
      Alert.alert("API Key Required", "Enter your Anthropic API key in the settings below to use cloud translation.");
      return;
    }

    setRunLoading(true);
    try {
      const granted = await lt.requestPermissions();
      if (!granted) {
        Alert.alert("Permission Required", "Grant the required permissions, then tap Start again.");
        return;
      }
      await lt.start({ targetLang, sourceLang, enableAudio: audioEnabled && asrReady, apiKey });
      setRunning(true);
    } catch (e: any) {
      Alert.alert("Error", e.message);
    } finally {
      setRunLoading(false);
    }
  }, [running, targetLang, audioEnabled, asrReady, apiKey]);

  // ── ASR model download ────────────────────────────────────────────────────
  const handleDownloadAsrModels = async () => {
    if (asrDownloading) return;
    setAsrDownloading(true);
    setAsrProgress({ downloaded: 0, total: 4 });
    const unsub = lt.onAsrModelDownloadProgress((downloaded, total) => {
      setAsrProgress({ downloaded, total });
    });
    try {
      await lt.downloadAsrModels();
      setAsrReady(true);
    } catch (e: any) {
      Alert.alert("Download Failed", e.message ?? "Check your connection and try again.");
    } finally {
      unsub();
      setAsrDownloading(false);
    }
  };

  return (
    <ScrollView style={styles.root} contentContainerStyle={styles.content}>
      <BreathingBackground />

      <TouchableOpacity style={styles.back} onPress={() => router.back()}>
        <Text style={styles.backText}>{t.back}</Text>
      </TouchableOpacity>
      <Text style={styles.title}>{t.overlayTitle}</Text>
      <Text style={styles.subtitle}>Screen Capture · Cloud Translation</Text>

      {/* ── iOS placeholder ───────────────────────────────────────────── */}
      {Platform.OS !== "android" && (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Coming to iOS</Text>
          <Text style={styles.cardDesc}>
            Live translation via ScreenCaptureKit and Vision framework is planned for a future update.
          </Text>
        </View>
      )}

      {/* ── Android ───────────────────────────────────────────────────── */}
      {Platform.OS === "android" && (
        <>
          {/* Live Translation */}
          {lt.isAvailable() && (
            <View style={styles.card}>
              <Text style={styles.cardTitle}>{t.overlayTitle}</Text>
              <Text style={styles.cardDesc}>
                Captures your screen and audio, detects text with MLKit OCR, and translates via cloud API in real time.
              </Text>

              <Text style={styles.label}>Video subtitle language</Text>
              <Text style={[styles.cardDesc, { fontSize: 11, marginTop: -8 }]}>
                Selects the OCR script. Choose the language shown in the video subtitles.
              </Text>
              <View style={styles.chipRow}>
                {[
                  { code: "auto", label: "Auto (Latin)" },
                  { code: "zh",   label: "Chinese" },
                  { code: "ja",   label: "Japanese" },
                  { code: "ko",   label: "Korean" },
                  { code: "hi",   label: "Hindi" },
                ].map((opt) => (
                  <TouchableOpacity
                    key={opt.code}
                    style={[styles.chip, sourceLang === opt.code && styles.chipActive]}
                    onPress={() => !running && setSourceLang(opt.code)}
                  >
                    <Text style={[styles.chipText, sourceLang === opt.code && styles.chipTextActive]}>
                      {opt.label}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>

              <Text style={styles.label}>Translate to</Text>
              <LanguagePicker
                value={targetLang}
                onChange={(lang) => setTargetLang(lang.code)}
                searchPlaceholder={t.searchLanguage}
              />

              {/* Audio toggle — configured before session starts */}
              <View style={styles.statusRow}>
                <View style={{ flex: 1 }}>
                  <Text style={styles.label}>Audio translation</Text>
                  <Text style={[styles.cardDesc, { fontSize: 11 }]}>
                    {asrReady
                      ? "Uses on-device ASR alongside OCR"
                      : "Download ASR model below to enable"}
                  </Text>
                </View>
                <Switch
                  value={audioEnabled && asrReady}
                  onValueChange={setAudioEnabled}
                  disabled={!asrReady || running}
                  trackColor={{ false: "rgba(255,255,255,0.1)", true: "#7170ff" }}
                  thumbColor={audioEnabled && asrReady ? "#f7f8f8" : "#62666d"}
                />
              </View>

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
                  <Text style={styles.subtitleSource}>LIVE</Text>
                  {subtitle.original !== subtitle.translated && (
                    <Text style={styles.subtitleOriginal}>{subtitle.original}</Text>
                  )}
                  <Text style={styles.subtitleTranslated}>{subtitle.translated}</Text>
                </View>
              )}
            </View>
          )}

          {/* API Key */}
          <View style={styles.card}>
            <View style={styles.statusRow}>
              <View style={[styles.dot, { backgroundColor: apiKey.trim() ? "#4caf50" : "#ff6b6b" }]} />
              <Text style={styles.cardTitle}>Anthropic API Key</Text>
            </View>
            <Text style={styles.cardDesc}>
              Required for cloud streaming translation. Stored locally on device only.
            </Text>
            <View style={styles.inputRow}>
              <TextInput
                style={styles.input}
                value={apiKey}
                onChangeText={setApiKey}
                placeholder="sk-ant-..."
                placeholderTextColor="#62666d"
                secureTextEntry={!showApiKey}
                autoCapitalize="none"
                autoCorrect={false}
              />
              <TouchableOpacity
                style={styles.inputToggle}
                onPress={() => setShowApiKey((v) => !v)}
              >
                <Text style={styles.inputToggleText}>{showApiKey ? "Hide" : "Show"}</Text>
              </TouchableOpacity>
            </View>
          </View>

          {/* ASR Model */}
          {lt.isAvailable() && (
            <View style={styles.card}>
              <View style={styles.statusRow}>
                <View style={[styles.dot, { backgroundColor: asrReady ? "#4caf50" : "#62666d" }]} />
                <Text style={styles.cardTitle}>Audio Recognition Model</Text>
              </View>
              <Text style={styles.cardDesc}>
                On-device speech recognition via Sherpa-ONNX (~44 MB, one-time download).
                Required for audio translation.
              </Text>
              {asrProgress && !asrReady && (
                <View style={styles.progressRow}>
                  <View style={styles.progressBar}>
                    <View
                      style={[
                        styles.progressFill,
                        { width: `${(asrProgress.downloaded / asrProgress.total) * 100}%` },
                      ]}
                    />
                  </View>
                  <Text style={styles.progressText}>
                    {asrProgress.downloaded}/{asrProgress.total} files
                  </Text>
                </View>
              )}
              {asrReady ? (
                <View style={styles.statusRow}>
                  <View style={[styles.dot, { backgroundColor: "#4caf50" }]} />
                  <Text style={[styles.statusText, { color: "#4caf50" }]}>Ready</Text>
                </View>
              ) : (
                <TouchableOpacity
                  style={[styles.button, asrDownloading && styles.buttonDisabled]}
                  onPress={handleDownloadAsrModels}
                  disabled={asrDownloading}
                >
                  {asrDownloading
                    ? <ActivityIndicator color="#08090a" size="small" />
                    : <Text style={styles.buttonText}>Download (~44 MB)</Text>
                  }
                </TouchableOpacity>
              )}
            </View>
          )}
        </>
      )}
    </ScrollView>
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
  label: { color: "#d0d6e0", fontSize: 13, fontWeight: "500" },
  button: {
    backgroundColor: "#7170ff",
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: "center",
  },
  buttonDisabled: { opacity: 0.5 },
  buttonText: { color: "#08090a", fontWeight: "600", fontSize: 14 },
  subtitleBox: { gap: 4, paddingTop: 4 },
  subtitleSource: { color: "#7170ff", fontSize: 11, textTransform: "uppercase", letterSpacing: 0.5 },
  subtitleOriginal: { color: "#62666d", fontSize: 12 },
  subtitleTranslated: { color: "#f7f8f8", fontSize: 15, lineHeight: 22 },
  inputRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  input: {
    flex: 1,
    backgroundColor: "rgba(255,255,255,0.05)",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.1)",
    paddingHorizontal: 12,
    paddingVertical: 10,
    color: "#f7f8f8",
    fontSize: 13,
    fontFamily: "monospace",
  },
  inputToggle: {
    paddingHorizontal: 10,
    paddingVertical: 10,
  },
  inputToggleText: { color: "#7170ff", fontSize: 13 },
  chipRow: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  chip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.15)",
    backgroundColor: "rgba(255,255,255,0.05)",
  },
  chipActive: { borderColor: "#7170ff", backgroundColor: "rgba(113,112,255,0.15)" },
  chipText: { color: "#62666d", fontSize: 12 },
  chipTextActive: { color: "#7170ff", fontWeight: "600" },
  progressRow: { flexDirection: "row", alignItems: "center", gap: 10 },
  progressBar: { flex: 1, height: 4, borderRadius: 2, backgroundColor: "rgba(255,255,255,0.08)" },
  progressFill: { height: 4, borderRadius: 2, backgroundColor: "#7170ff" },
  progressText: { color: "#62666d", fontSize: 12, minWidth: 70 },
});
