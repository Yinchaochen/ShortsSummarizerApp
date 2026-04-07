import { useState, useEffect } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  StyleSheet, ScrollView, ActivityIndicator,
} from "react-native";
import { router } from "expo-router";
import { supabase } from "../src/lib/supabase";
import { submitSummarize } from "../src/lib/api";
import BreathingBackground from "../src/components/BreathingBackground";
import LanguagePicker from "../src/components/LanguagePicker";
import { useLanguage } from "./_layout";
import { LANGUAGES } from "../src/lib/languages";

export default function HomeScreen() {
  const { langCode, t, setLangCode } = useLanguage();
  const [url, setUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      if (!data.session) router.replace("/login");
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
      <View style={styles.header}>
        <Text style={styles.logo}>{t.appName}</Text>
        <TouchableOpacity onPress={handleSignOut}>
          <Text style={styles.signOut}>{t.signOut}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.hero}>
        <Text style={styles.title}>{t.title}</Text>
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

        {error ? <Text style={styles.error}>{error}</Text> : null}

        <TouchableOpacity style={styles.button} onPress={handleSubmit} disabled={loading || !url.trim()}>
          {loading
            ? <ActivityIndicator color="#08090a" />
            : <Text style={styles.buttonText}>{t.summarizeButton}</Text>
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
  button: {
    backgroundColor: "#7170ff",
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: "center",
  },
  buttonText: { color: "#08090a", fontWeight: "600", fontSize: 15 },
  error: { color: "#ff6b6b", fontSize: 13 },
});
