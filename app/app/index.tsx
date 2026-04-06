import { useState, useEffect } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  StyleSheet, ScrollView, ActivityIndicator,
} from "react-native";
import { router } from "expo-router";
import { supabase } from "../src/lib/supabase";
import { submitSummarize } from "../src/lib/api";
import BreathingBackground from "../src/components/BreathingBackground";

const LANGUAGES = [
  { code: "zh", label: "中文" },
  { code: "en", label: "English" },
  { code: "ja", label: "日本語" },
  { code: "ko", label: "한국어" },
  { code: "es", label: "Español" },
  { code: "fr", label: "Français" },
];

export default function HomeScreen() {
  const [url, setUrl] = useState("");
  const [language, setLanguage] = useState("en");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [userEmail, setUserEmail] = useState("");

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      if (!data.session) {
        router.replace("/login");
      } else {
        setUserEmail(data.session.user.email ?? "");
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
      const jobId = await submitSummarize(url.trim(), language);
      router.push({ pathname: "/result", params: { jobId } });
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <BreathingBackground />
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.logo}>Shorts Summarizer</Text>
        <TouchableOpacity onPress={handleSignOut}>
          <Text style={styles.signOut}>Sign out</Text>
        </TouchableOpacity>
      </View>

      {/* Hero */}
      <View style={styles.hero}>
        <Text style={styles.title}>Understand any{"\n"}short video instantly</Text>
        <Text style={styles.subtitle}>Paste a TikTok link and get a detailed summary</Text>
      </View>

      {/* Input card */}
      <View style={styles.card}>
        <TextInput
          style={styles.input}
          placeholder="Paste TikTok URL here..."
          placeholderTextColor="#62666d"
          value={url}
          onChangeText={setUrl}
          autoCapitalize="none"
          autoCorrect={false}
        />

        {/* Language picker */}
        <Text style={styles.label}>Summary language</Text>
        <View style={styles.langRow}>
          {LANGUAGES.map((lang) => (
            <TouchableOpacity
              key={lang.code}
              style={[styles.langChip, language === lang.code && styles.langChipActive]}
              onPress={() => setLanguage(lang.code)}
            >
              <Text style={[styles.langText, language === lang.code && styles.langTextActive]}>
                {lang.label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        {error ? <Text style={styles.error}>{error}</Text> : null}

        <TouchableOpacity style={styles.button} onPress={handleSubmit} disabled={loading || !url.trim()}>
          {loading
            ? <ActivityIndicator color="#08090a" />
            : <Text style={styles.buttonText}>Summarize →</Text>
          }
        </TouchableOpacity>
      </View>
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
  signOut: { color: "#62666d", fontSize: 14 },
  hero: { marginBottom: 48 },
  title: {
    fontSize: 40,
    fontWeight: "600",
    color: "#f7f8f8",
    letterSpacing: -1.5,
    lineHeight: 48,
    marginBottom: 12,
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
  langRow: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  langChip: {
    paddingHorizontal: 14,
    paddingVertical: 7,
    borderRadius: 9999,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    backgroundColor: "rgba(255,255,255,0.03)",
  },
  langChipActive: {
    backgroundColor: "rgba(113,112,255,0.15)",
    borderColor: "#7170ff",
  },
  langText: { color: "#62666d", fontSize: 13 },
  langTextActive: { color: "#7170ff", fontWeight: "500" },
  button: {
    backgroundColor: "#7170ff",
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: "center",
  },
  buttonText: { color: "#08090a", fontWeight: "600", fontSize: 15 },
  error: { color: "#ff6b6b", fontSize: 13 },
});
