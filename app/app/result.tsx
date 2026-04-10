import { useEffect, useState, useRef, useCallback } from "react";
import {
  View, Text, ScrollView, TouchableOpacity,
  StyleSheet, Animated, Clipboard,
} from "react-native";
import { router, useLocalSearchParams } from "expo-router";
import { pollJob } from "../src/lib/api";
import BreathingBackground from "../src/components/BreathingBackground";
import AIDetectionCard from "../src/components/AIDetectionCard";
import Footer from "../src/components/Footer";
import { useLanguage } from "./_layout";

const STEPS = ["downloading", "uploading", "processing", "analyzing"];

type JobResult = {
  summary: string;
  is_ai_generated: "yes" | "no" | "uncertain";
  is_deepfake: "yes" | "no" | "uncertain";
  ai_confidence: "high" | "medium" | "low";
  ai_reason: string;
};

function parseResult(raw: any): JobResult {
  if (raw && typeof raw === "object" && "summary" in raw) {
    return raw as JobResult;
  }
  // Legacy plain text result
  return {
    summary: typeof raw === "string" ? raw : String(raw),
    is_ai_generated: "uncertain",
    is_deepfake: "uncertain",
    ai_confidence: "low",
    ai_reason: "",
  };
}

export default function ResultScreen() {
  const { jobId } = useLocalSearchParams<{ jobId: string }>();
  const { t } = useLanguage();
  const [step, setStep] = useState("downloading");
  const [result, setResult] = useState<JobResult | null>(null);
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const progressAnim = useRef(new Animated.Value(0)).current;
  const toastOpacity = useRef(new Animated.Value(0)).current;

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

  const STEP_LABELS: Record<string, string> = {
    downloading: t.downloading,
    uploading: t.uploading,
    processing: t.processing,
    analyzing: t.analyzing,
  };

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
          setResult(parseResult(data.result));
          setDone(true);
        } else if (data.state === "error") {
          clearInterval(interval);
          if (data.detail === "VIDEO_TOO_LONG") {
            showVideoTooLongToast();
          } else {
            setError(data.detail ?? "Unknown error");
          }
        }
      } catch {
        clearInterval(interval);
        setError(t.failedToConnect);
      }
    }, 2000);
    return () => clearInterval(interval);
  }, [jobId]);

  const progressWidth = progressAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ["0%", "100%"],
  });

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
  copyButton: { borderWidth: 1, borderColor: "rgba(255,255,255,0.08)", borderRadius: 8, paddingVertical: 10, alignItems: "center" },
  copyText: { color: "#62666d", fontSize: 13 },
  toast: {
    position: "absolute",
    bottom: 48,
    left: 24,
    right: 24,
    backgroundColor: "rgba(18, 10, 10, 0.96)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255, 107, 107, 0.35)",
    paddingVertical: 14,
    paddingHorizontal: 18,
    alignItems: "center",
  },
  toastText: {
    color: "#ff8585",
    fontSize: 14,
    fontWeight: "500",
    textAlign: "center",
    lineHeight: 20,
  },
});
