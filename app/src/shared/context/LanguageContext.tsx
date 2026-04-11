import { createContext, useContext } from "react";
import { getTranslations, Translations } from "../lib/i18n";

export type LanguageContextType = {
  langCode: string;
  t: Translations;
  setLangCode: (code: string) => void;
};

export const LanguageContext = createContext<LanguageContextType>({
  langCode: "en",
  t: getTranslations("en"),
  setLangCode: () => {},
});

export function useLanguage(): LanguageContextType {
  return useContext(LanguageContext);
}
