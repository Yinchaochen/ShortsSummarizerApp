import { useEffect, useState, useRef } from "react";
import {
  View, Text, ScrollView, TouchableOpacity,
  StyleSheet, Animated, Clipboard,
} from "react-native";
import { router, useLocalSearchParams } from "expo-router";
import { pollJob } from "../src/lib/api";
import BreathingBackground from "../src/components/BreathingBackground";

const STEPS = ["downloading", "uploading", "processing", "analyzing"];

const STEP_LABELS: Record<string, string> = {
  downloading: "Downloading video...",
  uploading:   "Uploading to Gemini...",
  processing:  "Processing video...",
  analyzing:   "Analyzing content...",
};

export default function ResultScreen() {
  const { jobId } = useLocalSearchParams<{ jobId: string }>();
  const [step, setStep] = useState("downloading");
  const [result, setResult] = useState("");
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);
  const progressAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (!jobId) return;
    const interval = setInterval(async () => {
      try {
        const data = await pollJob(jobId);
        if (data.state === "progress" && data.label) {
          const currentStep = STEPS.indexOf(step);
          const newStep = data.label.toLowerCase().includes("download") ? "downloading"
            : data.label.toLowerCase().includes("upload") ? "uploading"
            : data.label.toLowerCase().includes("process") ? "processing"
            : "analyzing";
          setStep(newStep);
          const progress = (STEPS.indexOf(newStep) + 1) / STEPS.length;
          Animated.timing(progressAnim, {
            toValue: progress,
            duration: 400,
            useNativeDriver: false,
          }).start();
        } else if (data.state === "done") {
          clearInterval(interval);
          Animated.timing(progressAnim, { toValue: 1, duration: 300, useNativeDriver: false }).start();
          setResult(data.result ?? "");
          setDone(true);
        } else if (data.state === "error") {
          clearInterval(interval);
          setError(data.detail ?? "Unknown error");
        }
      } catch {
        clearInterval(interval);
        setError("Failed to connect to server");
      }
    }, 2000);
    return () => clearInterval(interval);
  }, [jobId]);

  const progressWidth = progressAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ["0%", "100%"],
  });

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <BreathingBackground />
      {/* Back */}
      <TouchableOpacity style={styles.back} onPress={() => router.back()}>
        <Text style={styles.backText}>← Back</Text>
      </TouchableOpacity>

      <Text style={styles.title}>{done ? "Summary ready" : "Analyzing video..."}</Text>

      {/* Progress bar */}
      {!done && !error && (
        <View style={styles.progressContainer}>
          <View style={styles.progressTrack}>
            <Animated.View style={[styles.progressFill, { width: progressWidth }]} />
          </View>
          <Text style={styles.stepLabel}>{STEP_LABELS[step] ?? step}</Text>
        </View>
      )}

      {/* Error */}
      {error ? (
        <View style={styles.errorCard}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      ) : null}

      {/* Result */}
      {result ? (
        <View style={styles.resultCard}>
          <Text style={styles.resultText}>{result}</Text>
          <TouchableOpacity
            style={styles.copyButton}
            onPress={() => Clipboard.setString(result)}
          >
            <Text style={styles.copyText}>Copy summary</Text>
          </TouchableOpacity>
        </View>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#08090a" },
  content: { paddingHorizontal: 24, paddingTop: 64, paddingBottom: 48 },
  back: { marginBottom: 32 },
  backText: { color: "#7170ff", fontSize: 15 },
  title: {
    fontSize: 28,
    fontWeight: "600",
    color: "#f7f8f8",
    letterSpacing: -0.8,
    marginBottom: 32,
  },
  progressContainer: { gap: 12, marginBottom: 32 },
  progressTrack: {
    height: 4,
    backgroundColor: "rgba(255,255,255,0.08)",
    borderRadius: 9999,
    overflow: "hidden",
  },
  progressFill: {
    height: "100%",
    backgroundColor: "#7170ff",
    borderRadius: 9999,
  },
  stepLabel: { color: "#62666d", fontSize: 13 },
  errorCard: {
    backgroundColor: "rgba(255,107,107,0.08)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,107,107,0.2)",
    padding: 16,
  },
  errorText: { color: "#ff6b6b", fontSize: 14, lineHeight: 22 },
  resultCard: {
    backgroundColor: "rgba(255,255,255,0.03)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    padding: 20,
    gap: 16,
  },
  resultText: {
    color: "#d0d6e0",
    fontSize: 15,
    lineHeight: 26,
  },
  copyButton: {
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    borderRadius: 8,
    paddingVertical: 10,
    alignItems: "center",
  },
  copyText: { color: "#62666d", fontSize: 13 },
});
