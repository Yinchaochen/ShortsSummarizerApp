import { useEffect, useState } from "react";
import { AppState, Platform } from "react-native";
import { Stack, router } from "expo-router";
import { supabase } from "../src/shared/lib/supabase";
import { Session } from "@supabase/supabase-js";
import { getTranslations } from "../src/shared/lib/i18n";
import { LanguageContext } from "../src/shared/context/LanguageContext";
import { useAppStore } from "../src/shared/store/useAppStore";
import { ScreenCaptureBridge } from "../src/features/live-translation/screen-capture-bridge";

export { useLanguage } from "../src/shared/context/LanguageContext";

export default function RootLayout() {
  const [session, setSession] = useState<Session | null>(null);
  const [langCode, setLangCodeState] = useState("en");
  const setUserId = useAppStore((s) => s.setUserId);

  // ── Share intent handler ─────────────────────────────────────────────────
  // Checks for a pending shared URL (from TikTok / YouTube share sheet) on
  // launch and whenever the app returns to the foreground.
  // Navigates to overlay-settings with the URL pre-filled for auto-trigger.
  useEffect(() => {
    if (Platform.OS !== "android") return;

    const checkSharedUrl = async () => {
      const url = await ScreenCaptureBridge.getSharedUrl().catch(() => null);
      if (url) {
        router.push({ pathname: "/overlay-settings", params: { sharedUrl: url } });
      }
    };

    checkSharedUrl();
    const sub = AppState.addEventListener("change", (state) => {
      if (state === "active") checkSharedUrl();
    });
    return () => sub.remove();
  }, []);

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      setSession(data.session);
      setUserId(data.session?.user?.id ?? null);
      const saved = data.session?.user?.user_metadata?.language;
      if (saved) setLangCodeState(saved);
    });

    const { data: listener } = supabase.auth.onAuthStateChange((event, session) => {
      setSession(session);
      setUserId(session?.user?.id ?? null);
      if (event === "SIGNED_IN") {
        const saved = session?.user?.user_metadata?.language;
        if (saved) setLangCodeState(saved);
      }
    });
    return () => listener.subscription.unsubscribe();
  }, []);

  function setLangCode(code: string) {
    setLangCodeState(code);
    supabase.auth.updateUser({ data: { language: code } });
  }

  return (
    <LanguageContext.Provider value={{ langCode, t: getTranslations(langCode), setLangCode }}>
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="index" />
        <Stack.Screen name="login" />
        <Stack.Screen name="result" />
        <Stack.Screen name="overlay-settings" />
      </Stack>
    </LanguageContext.Provider>
  );
}
