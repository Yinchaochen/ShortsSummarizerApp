import { View, Text, StyleSheet } from "react-native";
import { useLanguage } from "../../../shared/context/LanguageContext";

type Detection = "yes" | "no" | "uncertain";
type Confidence = "high" | "medium" | "low";

type Props = {
  isAiGenerated: Detection;
  isDeepfake: Detection;
  confidence: Confidence;
  reason: string;
};

const VERDICT_CONFIG = {
  yes:       { color: "#ff6b6b", bg: "rgba(255,107,107,0.08)", border: "rgba(255,107,107,0.25)", dot: "#ff6b6b" },
  no:        { color: "#4ade80", bg: "rgba(74,222,128,0.08)",  border: "rgba(74,222,128,0.25)",  dot: "#4ade80" },
  uncertain: { color: "#facc15", bg: "rgba(250,204,21,0.08)",  border: "rgba(250,204,21,0.25)",  dot: "#facc15" },
};

function DetectionRow({ label, verdict, verdictLabel }: { label: string; verdict: Detection; verdictLabel: string }) {
  const cfg = VERDICT_CONFIG[verdict];
  return (
    <View style={[styles.row, { backgroundColor: cfg.bg, borderColor: cfg.border }]}>
      <View style={styles.rowLeft}>
        <View style={[styles.dot, { backgroundColor: cfg.dot }]} />
        <Text style={styles.rowLabel}>{label}</Text>
      </View>
      <Text style={[styles.verdict, { color: cfg.color }]}>{verdictLabel}</Text>
    </View>
  );
}

export default function AIDetectionCard({ isAiGenerated, isDeepfake, confidence, reason }: Props) {
  const { t } = useLanguage();

  const verdictLabel: Record<Detection, string> = {
    yes: t.verdictDetected,
    no: t.verdictNotDetected,
    uncertain: t.verdictUncertain,
  };

  const confidenceLabel: Record<Confidence, string> = {
    high: t.confidenceHigh,
    medium: t.confidenceMedium,
    low: t.confidenceLow,
  };

  return (
    <View style={styles.card}>
      <View style={styles.header}>
        <Text style={styles.title}>{t.aiAnalysisTitle}</Text>
        <Text style={styles.confidence}>{confidenceLabel[confidence]}</Text>
      </View>

      <View style={styles.rows}>
        <DetectionRow label={t.aiGeneratedLabel} verdict={isAiGenerated} verdictLabel={verdictLabel[isAiGenerated]} />
        <DetectionRow label={t.deepfakeLabel} verdict={isDeepfake} verdictLabel={verdictLabel[isDeepfake]} />
      </View>

      {reason ? (
        <View style={styles.reasonBox}>
          <Text style={styles.reasonText}>{reason}</Text>
        </View>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "rgba(255,255,255,0.02)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    padding: 20,
    gap: 16,
    marginTop: 16,
  },
  header: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  title: { color: "#f7f8f8", fontSize: 14, fontWeight: "600", letterSpacing: 0.2 },
  confidence: { color: "#62666d", fontSize: 12 },
  rows: { gap: 8 },
  row: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  rowLeft: { flexDirection: "row", alignItems: "center", gap: 10 },
  dot: { width: 7, height: 7, borderRadius: 4 },
  rowLabel: { color: "#d0d6e0", fontSize: 13 },
  verdict: { fontSize: 13, fontWeight: "600" },
  reasonBox: { backgroundColor: "rgba(255,255,255,0.03)", borderRadius: 8, padding: 12 },
  reasonText: { color: "#62666d", fontSize: 12, lineHeight: 18 },
});
