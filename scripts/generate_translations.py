"""
Generate UI translations for all languages in languages.ts using Gemini.

Usage:
    cd "E:/Shorts Summarizer"
    pip install google-genai python-dotenv
    python scripts/generate_translations.py

Output: app/src/lib/i18n.ts (overwrites existing file)
"""

import os
import json
import time
import re
from pathlib import Path
from dotenv import load_dotenv
from google import genai
from google.genai import types

load_dotenv(Path(__file__).parent.parent / "backend" / ".env")

# ─── Source strings (English) ────────────────────────────────────────────────
# Keep this in sync with the Translations type in i18n.ts

ENGLISH = {
    "appName": "Uchia",
    "tagline": "Understand any video instantly",
    "title": "Understand any\nshort video instantly",
    "subtitle": "Paste a video link and get a detailed summary",
    "urlPlaceholder": "Paste the video URL/Link here...",
    "summaryLanguageLabel": "Summary language",
    "summarizeButton": "Summarize →",
    "signOut": "Sign out",
    "analyzingVideo": "Analyzing video...",
    "summaryReady": "Summary ready",
    "back": "← Back",
    "copySummary": "Copy summary",
    "downloading": "Downloading video...",
    "uploading": "Uploading to Gemini...",
    "processing": "Processing video...",
    "analyzing": "Analyzing content...",
    "signIn": "Sign in",
    "createAccount": "Create account",
    "noAccount": "No account? Sign up",
    "haveAccount": "Already have an account? Sign in",
    "failedToConnect": "Failed to connect to server",
    "searchLanguage": "Search language...",
    "videoTooLong": "Videos longer than 10 minutes are not supported",
    "overlayTitle": "Live Translation",
    "overlaySubtitle": "Phase 1 — Bridge Test",
    "overlayBridgeTitle": "Native Bridge",
    "overlayBridgeDesc": "Verifies the React Native ↔ Kotlin channel is working.",
    "overlayBridgeIdle": "Not tested yet",
    "overlayBridgePinging": "Pinging…",
    "overlayTestButton": "Test bridge →",
    "overlayPermissionsTitle": "Permissions",
    "overlayDrawPermission": "Draw over other apps",
    "overlayAccessibilityPerm": "Accessibility service",
    "overlayGrant": "Grant →",
    "overlayRefresh": "Refresh status",
    "overlayAndroidOnly": "Live Translation overlay is only available on Android.",
}

# ─── All languages (from languages.ts) ───────────────────────────────────────

LANGUAGES = [
    ("af", "Afrikaans"), ("sq", "Albanian"), ("am", "Amharic"), ("ar", "Arabic"),
    ("hy", "Armenian"), ("az", "Azerbaijani"), ("eu", "Basque"), ("be", "Belarusian"),
    ("bn", "Bengali"), ("bs", "Bosnian"), ("bg", "Bulgarian"), ("ca", "Catalan"),
    ("zh", "Chinese (Simplified)"), ("zh-TW", "Chinese (Traditional)"), ("hr", "Croatian"),
    ("cs", "Czech"), ("da", "Danish"), ("nl", "Dutch"), ("en", "English"),
    ("et", "Estonian"), ("fi", "Finnish"), ("fr", "French"), ("gl", "Galician"),
    ("ka", "Georgian"), ("de", "German"), ("el", "Greek"), ("gu", "Gujarati"),
    ("ht", "Haitian Creole"), ("ha", "Hausa"), ("he", "Hebrew"), ("hi", "Hindi"),
    ("hu", "Hungarian"), ("is", "Icelandic"), ("id", "Indonesian"), ("ga", "Irish"),
    ("it", "Italian"), ("ja", "Japanese"), ("jv", "Javanese"), ("kn", "Kannada"),
    ("kk", "Kazakh"), ("km", "Khmer"), ("ko", "Korean"), ("ku", "Kurdish"),
    ("lo", "Lao"), ("lv", "Latvian"), ("lt", "Lithuanian"), ("mk", "Macedonian"),
    ("ms", "Malay"), ("ml", "Malayalam"), ("mt", "Maltese"), ("mr", "Marathi"),
    ("mn", "Mongolian"), ("my", "Myanmar"), ("ne", "Nepali"), ("no", "Norwegian"),
    ("ps", "Pashto"), ("fa", "Persian"), ("pl", "Polish"), ("pt", "Portuguese"),
    ("pa", "Punjabi"), ("ro", "Romanian"), ("ru", "Russian"), ("sr", "Serbian"),
    ("si", "Sinhala"), ("sk", "Slovak"), ("sl", "Slovenian"), ("so", "Somali"),
    ("es", "Spanish"), ("sw", "Swahili"), ("sv", "Swedish"), ("tl", "Tagalog"),
    ("ta", "Tamil"), ("te", "Telugu"), ("th", "Thai"), ("tr", "Turkish"),
    ("uk", "Ukrainian"), ("ur", "Urdu"), ("uz", "Uzbek"), ("vi", "Vietnamese"),
    ("cy", "Welsh"), ("yi", "Yiddish"), ("yo", "Yoruba"), ("zu", "Zulu"),
]

# ─── Gemini translation ───────────────────────────────────────────────────────

def translate_batch(client, lang_code: str, lang_name: str, strings: dict) -> dict:
    """Ask Gemini to translate all strings to the target language in one call."""
    source_json = json.dumps(strings, ensure_ascii=False, indent=2)

    prompt = f"""Translate the following JSON strings from English to {lang_name} ({lang_code}).

Rules:
- Keep the JSON keys exactly as-is (do not translate keys)
- Keep special characters like →, ←, ↔, \n, ... exactly as-is
- Keep "Gemini" and "React Native" and "Kotlin" untranslated (they are brand/tech names)
- For "appName" use the most natural translation or keep "Shorts Summarizer" if no good translation exists
- Return ONLY valid JSON, no explanation, no markdown

Source JSON:
{source_json}
"""

    response = client.models.generate_content(
        model="models/gemini-2.5-flash",
        contents=prompt,
        config=types.GenerateContentConfig(
            temperature=0.1,
            response_mime_type="application/json",
        ),
    )

    text = response.text.strip()
    # Strip markdown fences if present
    text = re.sub(r"^```(?:json)?\s*", "", text)
    text = re.sub(r"\s*```$", "", text).strip()

    result = json.loads(text)

    # Validate all keys are present
    for key in strings:
        if key not in result:
            print(f"  ⚠ Missing key '{key}' for {lang_name}, using English fallback")
            result[key] = strings[key]

    return result


# ─── TypeScript file generator ────────────────────────────────────────────────

def build_ts_file(all_translations: dict[str, dict]) -> str:
    lines = [
        "// Auto-generated by scripts/generate_translations.py — do not edit manually.",
        "// Re-run the script to update translations.",
        "",
        "export type Translations = {",
    ]
    for key in ENGLISH:
        lines.append(f"  {key}: string;")
    lines += [
        "};",
        "",
        "const translations: Record<string, Translations> = {",
    ]

    for lang_code, strings in all_translations.items():
        lines.append(f"  {json.dumps(lang_code)}: {{")
        for key, value in strings.items():
            escaped = json.dumps(value, ensure_ascii=False)
            lines.append(f"    {key}: {escaped},")
        lines.append("  },")

    lines += [
        "};",
        "",
        "export function getTranslations(langCode: string): Translations {",
        "  return translations[langCode] ?? translations[\"en\"];",
        "}",
        "",
    ]
    return "\n".join(lines)


# ─── Main ─────────────────────────────────────────────────────────────────────

def main():
    api_key = os.environ.get("GOOGLE_API_KEY", "")
    if not api_key:
        raise SystemExit("GOOGLE_API_KEY not set in backend/.env")

    client = genai.Client(api_key=api_key)
    all_translations: dict[str, dict] = {}

    # English is the source — no translation needed
    all_translations["en"] = ENGLISH

    skip = {"en"}  # already done

    for lang_code, lang_name in LANGUAGES:
        if lang_code in skip:
            continue

        print(f"Translating -> {lang_name} ({lang_code})...", end=" ", flush=True)
        try:
            result = translate_batch(client, lang_code, lang_name, ENGLISH)
            all_translations[lang_code] = result
            print("OK")
        except Exception as e:
            print(f"FAILED ({e}) - using English fallback")
            all_translations[lang_code] = ENGLISH.copy()

        # Be polite to the API
        time.sleep(0.5)

    # Write output
    out_path = Path(__file__).parent.parent / "app" / "src" / "lib" / "i18n.ts"
    out_path.write_text(build_ts_file(all_translations), encoding="utf-8")
    print(f"\nDone. Written to {out_path}")
    print(f"   {len(all_translations)} languages, {len(ENGLISH)} strings each")


if __name__ == "__main__":
    main()
