import { useState } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  StyleSheet, KeyboardAvoidingView, Platform, ActivityIndicator,
} from "react-native";
import { router } from "expo-router";
import * as WebBrowser from "expo-web-browser";
import * as Linking from "expo-linking";
import { AntDesign } from "@expo/vector-icons";
import { supabase } from "../src/shared/lib/supabase";
import BreathingBackground from "../src/shared/components/BreathingBackground";
import Footer from "../src/shared/components/Footer";
import { useLanguage } from "../src/shared/context/LanguageContext";

WebBrowser.maybeCompleteAuthSession();

export default function LoginScreen() {
  const { t } = useLanguage();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSignUp, setIsSignUp] = useState(false);
  const [loading, setLoading] = useState(false);
  const [socialLoading, setSocialLoading] = useState<"google" | "apple" | null>(null);
  const [error, setError] = useState("");

  async function handleAuth() {
    setLoading(true);
    setError("");
    try {
      const { error } = isSignUp
        ? await supabase.auth.signUp({ email, password })
        : await supabase.auth.signInWithPassword({ email, password });
      if (error) throw error;
      router.replace("/");
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function signInWithProvider(provider: "google" | "apple") {
    setSocialLoading(provider);
    setError("");
    try {
      if (Platform.OS === "web") {
        const { error } = await supabase.auth.signInWithOAuth({
          provider,
          options: { redirectTo: window.location.origin },
        });
        if (error) throw error;
      } else {
        const redirectTo = Linking.createURL("/");
        const { data, error } = await supabase.auth.signInWithOAuth({
          provider,
          options: { redirectTo },
        });
        if (error) throw error;
        if (!data?.url) return;

        const result = await WebBrowser.openAuthSessionAsync(data.url, redirectTo);
        if (result.type === "success" && result.url) {
          const hash = result.url.split("#")[1] ?? "";
          const params = new URLSearchParams(hash);
          const accessToken = params.get("access_token");
          const refreshToken = params.get("refresh_token");
          if (accessToken && refreshToken) {
            await supabase.auth.setSession({ access_token: accessToken, refresh_token: refreshToken });
            router.replace("/");
          }
        }
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setSocialLoading(null);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
    >
      <BreathingBackground />
      <Text style={styles.title}>{t.appName}</Text>
      <Text style={styles.subtitle}>{t.tagline}</Text>

      <View style={styles.card}>
        {/* Social login */}
        <TouchableOpacity
          style={styles.socialButton}
          onPress={() => signInWithProvider("google")}
          disabled={socialLoading !== null}
        >
          {socialLoading === "google"
            ? <ActivityIndicator color="#08090a" size="small" />
            : <AntDesign name="google" size={18} color="#08090a" style={styles.socialIcon} />
          }
          <Text style={styles.socialButtonText}>Continue with Google</Text>
        </TouchableOpacity>

        {/* Divider */}
        <View style={styles.divider}>
          <View style={styles.dividerLine} />
          <Text style={styles.dividerText}>or</Text>
          <View style={styles.dividerLine} />
        </View>

        {/* Email / password */}
        <TextInput
          style={styles.input}
          placeholder="Email"
          placeholderTextColor="#62666d"
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
        />
        <TextInput
          style={styles.input}
          placeholder="Password"
          placeholderTextColor="#62666d"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />

        {error ? <Text style={styles.error}>{error}</Text> : null}

        <TouchableOpacity style={styles.button} onPress={handleAuth} disabled={loading}>
          {loading
            ? <ActivityIndicator color="#08090a" />
            : <Text style={styles.buttonText}>{isSignUp ? t.createAccount : t.signIn}</Text>
          }
        </TouchableOpacity>

        <TouchableOpacity onPress={() => setIsSignUp(!isSignUp)}>
          <Text style={styles.toggle}>
            {isSignUp ? t.haveAccount : t.noAccount}
          </Text>
        </TouchableOpacity>
      </View>
      <Footer />
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#08090a",
    justifyContent: "center",
    paddingHorizontal: 24,
  },
  title: {
    fontSize: 32,
    fontWeight: "600",
    color: "#f7f8f8",
    letterSpacing: -1,
    marginBottom: 8,
    textAlign: "center",
  },
  subtitle: {
    fontSize: 15,
    color: "#62666d",
    textAlign: "center",
    marginBottom: 48,
  },
  card: {
    backgroundColor: "rgba(255,255,255,0.03)",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    padding: 24,
    gap: 12,
  },
  socialButton: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#f7f8f8",
    borderRadius: 8,
    paddingVertical: 13,
    gap: 8,
  },
  appleButton: {
    backgroundColor: "#1a1a1a",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.15)",
  },
  socialIcon: { width: 20, textAlign: "center" },
  socialButtonText: {
    color: "#08090a",
    fontWeight: "600",
    fontSize: 15,
  },
  appleButtonText: { color: "#f7f8f8" },
  divider: {
    flexDirection: "row",
    alignItems: "center",
    marginVertical: 4,
    gap: 10,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: "rgba(255,255,255,0.08)",
  },
  dividerText: {
    color: "#62666d",
    fontSize: 13,
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
  button: {
    backgroundColor: "#7170ff",
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: "center",
    marginTop: 4,
  },
  buttonText: { color: "#08090a", fontWeight: "600", fontSize: 15 },
  toggle: {
    color: "#7170ff",
    textAlign: "center",
    fontSize: 14,
    marginTop: 4,
  },
  error: { color: "#ff6b6b", fontSize: 13, textAlign: "center" },
});
