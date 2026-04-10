import { createContext, useContext, useEffect, useState } from "react";
import { Stack } from "expo-router";
import { supabase } from "../src/lib/supabase";
import { Session } from "@supabase/supabase-js";
import { getTranslations, Translations } from "../src/lib/i18n";

type LanguageContextType = {
  langCode: string;
  t: Translations;
  setLangCode: (code: string) => void;
};

export const LanguageContext = createContext<LanguageContextType>({
  langCode: "en",
  t: getTranslations("en"),
  setLangCode: () => {},
});

export function useLanguage() {
  return useContext(LanguageContext);
}

export default function RootLayout() {
  const [session, setSession] = useState<Session | null>(null);
  const [langCode, setLangCodeState] = useState("en");

  useEffect(() => {
    // Load saved language once on startup
    supabase.auth.getSession().then(({ data }) => {
      setSession(data.session);
      const saved = data.session?.user?.user_metadata?.language;
      if (saved) setLangCodeState(saved);
    });

    const { data: listener } = supabase.auth.onAuthStateChange((event, session) => {
      setSession(session);
      // Only sync language on actual sign-in, not on token refreshes.
      // TOKEN_REFRESHED can carry a stale JWT that would override the
      // user's explicit language selection made moments earlier.
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
