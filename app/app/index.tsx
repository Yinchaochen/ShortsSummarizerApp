import { useState, useEffect } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  StyleSheet, ScrollView, ActivityIndicator,
} from "react-native";
import { router } from "expo-router";
import { supabase } from "../src/shared/lib/supabase";
import { submitSummarize, getUsage, ApiError } from "../src/features/summarizer/api";
import BreathingBackground from "../src/shared/components/BreathingBackground";
import LanguagePicker from "../src/shared/components/LanguagePicker";
import Footer from "../src/shared/components/Footer";
import { useLanguage } from "../src/shared/context/LanguageContext";
import { LANGUAGES } from "../src/shared/lib/languages";
import { useAppStore } from "../src/shared/store/useAppStore";

export default function HomeScreen() {
  const { langCode, t, setLangCode } = useLanguage();
  const { usageRemaining, setUsageRemaining } = useAppStore();
  const [url, setUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      if (!data.session) {
        router.replace("/login");
      } else {
        getUsage().then((u) => setUsageRemaining(u.remaining)).catch(() => {});
      }
    });
  }, []);

  async function handleSignOut() {
    await supabase.auth.signOut();
    router.replace("/login");
  }

  async function handleSubmit() {
    if (!url.trim()) return;
    setLoading(true);
    setError("");
    try {
      const jobId = await submitSummarize(url.trim(), langCode);
      getUsage().then((u) => setUsageRemaining(u.remaining)).catch(() => {});
      router.push({ pathname: "/result", params: { jobId } });
    } catch (e) {
      setError(e instanceof ApiError ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <BreathingBackground />
      <View style={styles.header}>
        <Text style={styles.logo}>{t.appName}</Text>
        <View style={styles.headerActions}>
          <TouchableOpacity onPress={() => router.push("/overlay-settings")} style={styles.overlayButton}>
            <Text style={styles.overlayButtonText}>Live ✦</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={handleSignOut}>
            <Text style={styles.signOut}>{t.signOut}</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.hero}>
        <View style={styles.titleWrapper}>
          <Text style={styles.titleGhost} numberOfLines={3}>{t.titleAlt}</Text>
          <Text style={styles.title}>{t.title}</Text>
        </View>
        <Text style={styles.subtitle}>{t.subtitle}</Text>
      </View>

      <View style={styles.card}>
        <TextInput
          style={styles.input}
          placeholder={t.urlPlaceholder}
          placeholderTextColor="#62666d"
          value={url}
          onChangeText={setUrl}
          autoCapitalize="none"
          autoCorrect={false}
        />

        <Text style={styles.label}>{t.summaryLanguageLabel}</Text>
        <LanguagePicker
          value={langCode}
          onChange={(lang) => setLangCode(lang.code)}
          searchPlaceholder={t.searchLanguage}
        />

        {usageRemaining !== null && (
          <Text style={styles.usage}>{usageRemaining} {t.usageFreeLeft}</Text>
        )}

        {error ? <Text style={styles.error}>{error}</Text> : null}

        <TouchableOpacity style={styles.button} onPress={handleSubmit} disabled={loading || !url.trim()}>
          {loading
            ? <ActivityIndicator color="#08090a" />
            : <Text style={styles.buttonText}>{t.summarizeButton}</Text>
          }
        </TouchableOpacity>
      </View>
      <Footer />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#08090a" },
  content: { paddingHorizontal: 24, paddingTop: 64, paddingBottom: 48 },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 64,
  },
  logo: { color: "#f7f8f8", fontWeight: "600", fontSize: 16 },
  headerActions: { flexDirection: "row", alignItems: "center", gap: 16 },
  overlayButton: {
    backgroundColor: "rgba(113,112,255,0.12)",
    borderRadius: 6,
    borderWidth: 1,
    borderColor: "rgba(113,112,255,0.3)",
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  overlayButtonText: { color: "#7170ff", fontSize: 13, fontWeight: "600" },
  signOut: { color: "#62666d", fontSize: 14 },
  hero: { marginBottom: 48 },
  titleWrapper: { marginBottom: 12 },
  title: {
    fontSize: 40,
    fontWeight: "600",
    color: "#f7f8f8",
    letterSpacing: -1.5,
    lineHeight: 48,
  },
  titleGhost: {
    position: "absolute",
    fontSize: 40,
    fontWeight: "600",
    color: "#7170ff",
    letterSpacing: -1.5,
    lineHeight: 48,
    opacity: 0.2,
    left: 5,
    top: 5,
  },
  subtitle: { fontSize: 16, color: "#62666d", lineHeight: 24 },
  card: {
    backgroundColor: "rgba(255,255,255,0.03)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    padding: 20,
    gap: 16,
  },
  input: {
    backgroundColor: "rgba(255,255,255,0.05)",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    color: "#f7f8f8",
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 15,
  },
  label: { color: "#d0d6e0", fontSize: 13, fontWeight: "500" },
  button: {
    backgroundColor: "#7170ff",
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: "center",
  },
  buttonText: { color: "#08090a", fontWeight: "600", fontSize: 15 },
  usage: { color: "#62666d", fontSize: 12, textAlign: "right" },
  error: { color: "#ff6b6b", fontSize: 13 },
});
