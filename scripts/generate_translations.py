"""
Generate UI translations for all languages in languages.ts using Gemini.

Usage:
    cd "E:/Shorts Summarizer"
    pip install google-genai python-dotenv
    python scripts/generate_translations.py                        # full regeneration (all keys, all languages)
    python scripts/generate_translations.py --patch titleAlt       # patch one key
    python scripts/generate_translations.py --patch key1,key2      # patch multiple keys

Workflow when adding new UI text:
    1. Add the new key + English string to the ENGLISH dict below.
    2. Run: python scripts/generate_translations.py --patch <new_key>
    3. Commit the updated i18n.ts.

Output: app/src/shared/lib/i18n.ts (overwrites existing file)
"""

import os
import json
import time
import re
import argparse
from pathlib import Path
from dotenv import load_dotenv
from google import genai
from google.genai import types

load_dotenv(Path(__file__).parent.parent / "backend" / ".env")

# ─── Source strings (English) ────────────────────────────────────────────────
# This is the single source of truth for all UI strings.
# To add new UI text:
#   1. Add the key and English value here.
#   2. Run: python scripts/generate_translations.py --patch <key_name>
# Never edit i18n.ts directly — it is auto-generated from this dict.

ENGLISH = {
    "appName": "Uchia",
    "tagline": "Understand any video instantly",
    "title": "Understand any\nshort video instantly",
    "titleAlt": "Understand that person instantly",
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
    "aiAnalysisTitle": "AI & Deepfake Analysis",
    "confidenceHigh": "High confidence",
    "confidenceMedium": "Medium confidence",
    "confidenceLow": "Low confidence",
    "verdictDetected": "Detected",
    "verdictNotDetected": "Not detected",
    "verdictUncertain": "Uncertain",
    "aiGeneratedLabel": "AI-Generated Content",
    "deepfakeLabel": "Deepfake",
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
- Keep special characters like →, ←, ↔, \\n, ... exactly as-is
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


# ─── i18n.ts parser ──────────────────────────────────────────────────────────

def parse_existing_i18n(path: Path) -> dict[str, dict]:
    """
    Parse the auto-generated i18n.ts and return all current translations.
    Returns a dict: { lang_code: { key: value, ... }, ... }
    """
    content = path.read_text(encoding="utf-8")
    result = {}

    # Match each language block: "lang_code": {\n...\n  },
    lang_re = re.compile(r'"([\w-]+)":\s*\{\n(.*?)\n  \},?', re.DOTALL)
    # Match individual key-value pairs: "    key: "value","
    kv_re = re.compile(r'    (\w+):\s*("(?:[^"\\]|\\.)*"),')

    for m in lang_re.finditer(content):
        lang_code = m.group(1)
        block = m.group(2)
        translations = {}
        for kv in kv_re.finditer(block):
            key = kv.group(1)
            try:
                value = json.loads(kv.group(2))
                translations[key] = value
            except json.JSONDecodeError:
                pass
        result[lang_code] = translations

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
        for key in ENGLISH:  # Only output keys defined in ENGLISH (keeps TypeScript type in sync)
            value = strings.get(key, ENGLISH[key])  # Fall back to English if key missing
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


# ─── Full regeneration mode ───────────────────────────────────────────────────

def full_mode(client, out_path: Path) -> None:
    """Translate all keys for all languages from scratch."""
    all_translations: dict[str, dict] = {}
    all_translations["en"] = ENGLISH

    for lang_code, lang_name in LANGUAGES:
        if lang_code == "en":
            continue

        print(f"Translating -> {lang_name} ({lang_code})...", end=" ", flush=True)
        try:
            result = translate_batch(client, lang_code, lang_name, ENGLISH)
            all_translations[lang_code] = result
            print("OK")
        except Exception as e:
            print(f"FAILED ({e}) - using English fallback")
            all_translations[lang_code] = ENGLISH.copy()

        time.sleep(0.5)

    out_path.write_text(build_ts_file(all_translations), encoding="utf-8")
    print(f"\nDone. Written to {out_path}")
    print(f"   {len(all_translations)} languages, {len(ENGLISH)} strings each")


# ─── Patch mode (translate only specified keys) ───────────────────────────────

def patch_mode(client, out_path: Path, keys_to_patch: list[str]) -> None:
    """
    Translate only the specified keys for all languages, merging into the
    existing translations. Much faster than a full regeneration.
    """
    # Validate keys
    invalid = [k for k in keys_to_patch if k not in ENGLISH]
    if invalid:
        raise SystemExit(f"Keys not found in ENGLISH dict: {invalid}\nAdd them to ENGLISH first.")

    patch_english = {k: ENGLISH[k] for k in keys_to_patch}
    print(f"Patching keys: {keys_to_patch}")
    print(f"English values: {patch_english}\n")

    # Load existing translations
    if not out_path.exists():
        raise SystemExit(f"Output file not found: {out_path}\nRun without --patch first to do a full generation.")

    existing = parse_existing_i18n(out_path)
    if not existing:
        raise SystemExit("Could not parse existing i18n.ts. Run without --patch to regenerate.")

    print(f"Loaded existing translations for {len(existing)} languages.")

    all_translations: dict[str, dict] = {}

    for lang_code, lang_name in LANGUAGES:
        base = existing.get(lang_code, ENGLISH.copy())

        if lang_code == "en":
            all_translations["en"] = {**base, **patch_english}
            continue

        print(f"Patching {lang_name} ({lang_code})...", end=" ", flush=True)
        try:
            new_vals = translate_batch(client, lang_code, lang_name, patch_english)
            all_translations[lang_code] = {**base, **new_vals}
            print("OK")
        except Exception as e:
            print(f"FAILED ({e}) - keeping English fallback for patched keys")
            all_translations[lang_code] = {**base, **patch_english}

        time.sleep(0.5)

    out_path.write_text(build_ts_file(all_translations), encoding="utf-8")
    print(f"\nDone. Patched {keys_to_patch} across {len(all_translations)} languages.")
    print(f"Written to {out_path}")


# ─── Main ─────────────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(description="Generate/update i18n.ts translations via Gemini.")
    parser.add_argument(
        "--patch",
        metavar="KEY[,KEY...]",
        help="Translate only these comma-separated keys and merge into existing i18n.ts. "
             "Much faster than a full run. Example: --patch titleAlt",
    )
    args = parser.parse_args()

    api_key = os.environ.get("GOOGLE_API_KEY", "")
    if not api_key:
        raise SystemExit("GOOGLE_API_KEY not set in backend/.env")

    client = genai.Client(api_key=api_key)
    out_path = Path(__file__).parent.parent / "app" / "src" / "shared" / "lib" / "i18n.ts"

    if args.patch:
        keys = [k.strip() for k in args.patch.split(",") if k.strip()]
        patch_mode(client, out_path, keys)
    else:
        full_mode(client, out_path)


if __name__ == "__main__":
    main()
