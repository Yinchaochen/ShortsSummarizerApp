import { useEffect, useState, useRef, useCallback } from "react";
import {
  View, Text, ScrollView, TouchableOpacity,
  StyleSheet, Animated, Clipboard, ActivityIndicator,
} from "react-native";
import { router, useLocalSearchParams } from "expo-router";
import { pollJob, ApiError, fetchTranscript } from "../src/features/summarizer/api";
import { JobResult, TranscriptResult } from "../src/features/summarizer/types";
import { useAppStore } from "../src/shared/store/useAppStore";
import BreathingBackground from "../src/shared/components/BreathingBackground";
import AIDetectionCard from "../src/features/summarizer/components/AIDetectionCard";
import Footer from "../src/shared/components/Footer";
import { useLanguage } from "../src/shared/context/LanguageContext";
import { LANGUAGES } from "../src/shared/lib/languages";

const STEPS = ["downloading", "uploading", "processing", "analyzing"];

function parseResult(raw: any): JobResult {
  if (raw && typeof raw === "object" && "summary" in raw) {
    return raw as JobResult;
  }
  return {
    summary: typeof raw === "string" ? raw : String(raw),
    is_ai_generated: "uncertain",
    is_deepfake: "uncertain",
    ai_confidence: "low",
    ai_reason: "",
  };
}

export default function ResultScreen() {
  const { jobId, url, language } = useLocalSearchParams<{ jobId: string; url?: string; language?: string }>();
  const { langCode, t } = useLanguage();
  const addSummary = useAppStore((s) => s.addSummary);
  const transcriptLanguage = language || langCode;

  const [step, setStep] = useState("downloading");
  const [result, setResult] = useState<JobResult | null>(null);
  const [transcript, setTranscript] = useState<TranscriptResult | null>(null);
  const [transcriptLoading, setTranscriptLoading] = useState(false);
  const [transcriptError, setTranscriptError] = useState("");
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const progressAnim = useRef(new Animated.Value(0)).current;
  const toastOpacity = useRef(new Animated.Value(0)).current;

  const STEP_LABELS: Record<string, string> = {
    downloading: t.downloading,
    uploading: t.uploading,
    processing: t.processing,
    analyzing: t.analyzing,
  };

  const showVideoTooLongToast = useCallback(() => {
    setShowToast(true);
    Animated.sequence([
      Animated.timing(toastOpacity, { toValue: 1, duration: 300, useNativeDriver: true }),
      Animated.delay(2500),
      Animated.timing(toastOpacity, { toValue: 0, duration: 600, useNativeDriver: true }),
    ]).start(() => {
      setShowToast(false);
      router.back();
    });
  }, [toastOpacity]);

  useEffect(() => {
    if (!jobId) return;
    const interval = setInterval(async () => {
      try {
        const data = await pollJob(jobId);

        if (data.state === "progress" && data.label) {
          const newStep = data.label.toLowerCase().includes("download") ? "downloading"
            : data.label.toLowerCase().includes("upload") ? "uploading"
            : data.label.toLowerCase().includes("process") ? "processing"
            : "analyzing";
          setStep(newStep);
          const progress = (STEPS.indexOf(newStep) + 1) / STEPS.length;
          Animated.timing(progressAnim, { toValue: progress, duration: 400, useNativeDriver: false }).start();

        } else if (data.state === "done") {
          clearInterval(interval);
          Animated.timing(progressAnim, { toValue: 1, duration: 300, useNativeDriver: false }).start();
          const parsed = parseResult(data.result);
          setResult(parsed);
          setDone(true);
          // Store in history for future history screen
          if (jobId) {
            addSummary({ id: jobId, url: url ?? "", platform: "", language: transcriptLanguage, result: parsed });
          }

        } else if (data.state === "error") {
          clearInterval(interval);
          const code = data.code ?? data.detail ?? "";
          if (code === "VIDEO_TOO_LONG") {
            showVideoTooLongToast();
          } else {
            setError(data.detail ?? "Unknown error");
          }
        }
      } catch (e) {
        clearInterval(interval);
        setError(e instanceof ApiError ? e.message : t.failedToConnect);
      }
    }, 2000);
    return () => clearInterval(interval);
  }, [jobId, url, transcriptLanguage]);

  const getLanguageLabel = useCallback((code?: string, fallback?: string) => {
    if (!code) return fallback ?? "Unknown";
    const match = LANGUAGES.find((item) => item.code.toLowerCase() === code.toLowerCase());
    return match?.nativeName ?? match?.name ?? fallback ?? code;
  }, []);

  const handleExtractTranscript = useCallback(async () => {
    if (!url || transcriptLoading) return;
    setTranscriptLoading(true);
    setTranscriptError("");
    try {
      const data = await fetchTranscript(url, transcriptLanguage);
      setTranscript(data);
    } catch (e) {
      setTranscriptError(e instanceof ApiError ? e.message : t.failedToConnect);
    } finally {
      setTranscriptLoading(false);
    }
  }, [transcriptLanguage, transcriptLoading, t.failedToConnect, url]);

  const progressWidth = progressAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ["0%", "100%"],
  });

  const transcriptButtonLabel = transcript ? "Refresh Full Transcript" : "Extract Full Transcript";
  const transcriptSourceLabel = transcript
    ? getLanguageLabel(transcript.source_language.code, transcript.source_language.label)
    : "";
  const transcriptTargetLabel = transcript
    ? getLanguageLabel(transcript.target_language.code, transcript.target_language.label)
    : "";

  return (
    <View style={styles.rootContainer}>
      <ScrollView style={styles.container} contentContainerStyle={styles.content}>
        <BreathingBackground />
        <TouchableOpacity style={styles.back} onPress={() => router.back()}>
          <Text style={styles.backText}>{t.back}</Text>
        </TouchableOpacity>

        <Text style={styles.title}>{done ? t.summaryReady : t.analyzingVideo}</Text>

        {!done && !error && (
          <View style={styles.progressContainer}>
            <View style={styles.progressTrack}>
              <Animated.View style={[styles.progressFill, { width: progressWidth }]} />
            </View>
            <Text style={styles.stepLabel}>{STEP_LABELS[step] ?? step}</Text>
          </View>
        )}

        {error ? (
          <View style={styles.errorCard}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        ) : null}

        {result ? (
          <>
            <View style={styles.resultCard}>
              <Text style={styles.resultText}>{result.summary}</Text>
              <TouchableOpacity
                style={styles.copyButton}
                onPress={() => Clipboard.setString(result.summary)}
              >
                <Text style={styles.copyText}>{t.copySummary}</Text>
              </TouchableOpacity>
            </View>

            {url ? (
              <View style={styles.resultCard}>
                <View style={styles.transcriptHeader}>
                  <View style={styles.transcriptTitleWrap}>
                    <Text style={styles.transcriptTitle}>Full Transcript</Text>
                    <Text style={styles.transcriptMeta}>
                      {transcript?.is_bilingual
                        ? `${transcriptSourceLabel} + ${transcriptTargetLabel}`
                        : transcriptSourceLabel || "Original subtitles"}
                    </Text>
                  </View>
                  <TouchableOpacity
                    style={[styles.copyButton, styles.transcriptActionButton]}
                    onPress={handleExtractTranscript}
                    disabled={transcriptLoading}
                  >
                    {transcriptLoading
                      ? <ActivityIndicator color="#d0d6e0" />
                      : <Text style={styles.copyText}>{transcriptButtonLabel}</Text>
                    }
                  </TouchableOpacity>
                </View>

                {transcriptError ? (
                  <Text style={styles.transcriptError}>{transcriptError}</Text>
                ) : null}

                {transcript ? (
                  <>
                    <Text style={styles.transcriptBody}>{transcript.display_text}</Text>
                    <TouchableOpacity
                      style={styles.copyButton}
                      onPress={() => Clipboard.setString(transcript.display_text)}
                    >
                      <Text style={styles.copyText}>Copy Transcript</Text>
                    </TouchableOpacity>
                  </>
                ) : null}
              </View>
            ) : null}
            <AIDetectionCard
              isAiGenerated={result.is_ai_generated}
              isDeepfake={result.is_deepfake}
              confidence={result.ai_confidence}
              reason={result.ai_reason}
            />
          </>
        ) : null}

        <Footer />
      </ScrollView>

      {showToast && (
        <Animated.View style={[styles.toast, { opacity: toastOpacity }]}>
          <Text style={styles.toastText}>{t.videoTooLong}</Text>
        </Animated.View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  rootContainer: { flex: 1 },
  container: { flex: 1, backgroundColor: "#08090a" },
  content: { paddingHorizontal: 24, paddingTop: 64, paddingBottom: 48 },
  back: { marginBottom: 32 },
  backText: { color: "#7170ff", fontSize: 15 },
  title: { fontSize: 28, fontWeight: "600", color: "#f7f8f8", letterSpacing: -0.8, marginBottom: 32 },
  progressContainer: { gap: 12, marginBottom: 32 },
  progressTrack: { height: 4, backgroundColor: "rgba(255,255,255,0.08)", borderRadius: 9999, overflow: "hidden" },
  progressFill: { height: "100%", backgroundColor: "#7170ff", borderRadius: 9999 },
  stepLabel: { color: "#62666d", fontSize: 13 },
  errorCard: { backgroundColor: "rgba(255,107,107,0.08)", borderRadius: 12, borderWidth: 1, borderColor: "rgba(255,107,107,0.2)", padding: 16 },
  errorText: { color: "#ff6b6b", fontSize: 14, lineHeight: 22 },
  resultCard: { backgroundColor: "rgba(255,255,255,0.03)", borderRadius: 12, borderWidth: 1, borderColor: "rgba(255,255,255,0.08)", padding: 20, gap: 16 },
  resultText: { color: "#d0d6e0", fontSize: 15, lineHeight: 26 },
  transcriptHeader: { flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start", gap: 16 },
  transcriptTitleWrap: { flex: 1, gap: 6 },
  transcriptTitle: { color: "#f7f8f8", fontSize: 18, fontWeight: "600" },
  transcriptMeta: { color: "#62666d", fontSize: 12, lineHeight: 18 },
  transcriptActionButton: { minWidth: 180 },
  transcriptBody: { color: "#d0d6e0", fontSize: 14, lineHeight: 24 },
  transcriptError: { color: "#ff8585", fontSize: 13, lineHeight: 20 },
  copyButton: { borderWidth: 1, borderColor: "rgba(255,255,255,0.08)", borderRadius: 8, paddingVertical: 10, alignItems: "center" },
  copyText: { color: "#62666d", fontSize: 13 },
  toast: {
    position: "absolute",
    bottom: 48, left: 24, right: 24,
    backgroundColor: "rgba(18,10,10,0.96)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,107,107,0.35)",
    paddingVertical: 14,
    paddingHorizontal: 18,
    alignItems: "center",
  },
  toastText: { color: "#ff8585", fontSize: 14, fontWeight: "500", textAlign: "center", lineHeight: 20 },
});
