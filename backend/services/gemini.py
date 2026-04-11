import os
import re
import json
import time
from google import genai
from google.genai import types

GEMINI_MODELS = [
    "models/gemini-2.5-flash",
    "models/gemini-flash-latest",
]

LANG_PROMPTS = {
    "af": "Reply in Afrikaans.",
    "sq": "Përgjigju në gjuhën shqipe.",
    "am": "በአማርኛ ይመልሱ።",
    "ar": "الرجاء الرد باللغة العربية.",
    "hy": "Պատասխանեք հայերեն.",
    "az": "Azərbaycan dilində cavab verin.",
    "eu": "Erantzun euskaraz.",
    "be": "Адкажыце па-беларуску.",
    "bn": "বাংলায় উত্তর দিন।",
    "bs": "Odgovorite na bosanskom.",
    "bg": "Отговорете на български.",
    "ca": "Respon en català.",
    "zh": "请用中文（简体）回复。",
    "zh-TW": "請用中文（繁體）回覆。",
    "hr": "Odgovorite na hrvatskom.",
    "cs": "Odpovězte v češtině.",
    "da": "Svar på dansk.",
    "nl": "Antwoord in het Nederlands.",
    "en": "Reply in English.",
    "et": "Vasta eesti keeles.",
    "fi": "Vastaa suomeksi.",
    "fr": "Veuillez répondre en français.",
    "gl": "Responde en galego.",
    "ka": "უპასუხეთ ქართულად.",
    "de": "Bitte antworte auf Deutsch.",
    "el": "Απαντήστε στα ελληνικά.",
    "gu": "ગુજરાતીમાં જવાબ આપો.",
    "ht": "Repon an kreyòl ayisyen.",
    "ha": "Amsa da Hausa.",
    "he": "אנא השב בעברית.",
    "hi": "हिन्दी में उत्तर दें।",
    "hu": "Válaszoljon magyarul.",
    "is": "Svaraðu á íslensku.",
    "id": "Jawab dalam Bahasa Indonesia.",
    "ga": "Freagair as Gaeilge.",
    "it": "Rispondi in italiano.",
    "ja": "日本語で回答してください。",
    "jv": "Wangsulan nganggo Basa Jawa.",
    "kn": "ಕನ್ನಡದಲ್ಲಿ ಉತ್ತರಿಸಿ.",
    "kk": "Қазақша жауап беріңіз.",
    "km": "ឆ្លើយជាភាសាខ្មែរ។",
    "ko": "한국어로 답변해 주세요.",
    "ku": "Bi Kurdî bersiv bide.",
    "lo": "ຕອບເປັນພາສາລາວ.",
    "lv": "Atbildiet latviešu valodā.",
    "lt": "Atsakykite lietuvių kalba.",
    "mk": "Одговорете на македонски.",
    "ms": "Jawab dalam Bahasa Melayu.",
    "ml": "മലയാളത്തിൽ ഉത്തരം നൽകുക.",
    "mt": "Wieġeb bil-Malti.",
    "mr": "मराठीत उत्तर द्या.",
    "mn": "Монгол хэлээр хариулна уу.",
    "my": "မြန်မာဘာသာဖြင့် ဖြေဆိုပါ။",
    "ne": "नेपालीमा जवाफ दिनुहोस्।",
    "no": "Svar på norsk.",
    "ps": "پښتو کې ځواب ورکړئ.",
    "fa": "لطفاً به فارسی پاسخ دهید.",
    "pl": "Odpowiedz po polsku.",
    "pt": "Por favor, responda em português.",
    "pa": "ਪੰਜਾਬੀ ਵਿੱਚ ਜਵਾਬ ਦਿਓ।",
    "ro": "Răspundeți în română.",
    "ru": "Пожалуйста, ответьте по-русски.",
    "sr": "Одговорите на српском.",
    "si": "සිංහලෙන් පිළිතුරු දෙන්න.",
    "sk": "Odpovedzte v slovenčine.",
    "sl": "Odgovorite v slovenščini.",
    "so": "Ku jawaab Soomaali.",
    "es": "Por favor responde en español.",
    "sw": "Jibu kwa Kiswahili.",
    "sv": "Svara på svenska.",
    "tl": "Sumagot sa Filipino.",
    "ta": "தமிழில் பதில் அளிக்கவும்.",
    "te": "తెలుగులో సమాధానం ఇవ్వండి.",
    "th": "กรุณาตอบเป็นภาษาไทย",
    "tr": "Lütfen Türkçe yanıt verin.",
    "uk": "Будь ласка, відповідайте українською.",
    "ur": "براہ کرم اردو میں جواب دیں۔",
    "uz": "O'zbek tilida javob bering.",
    "vi": "Vui lòng trả lời bằng tiếng Việt.",
    "cy": "Atebwch yn Gymraeg.",
    "yi": "ענטפערט אויף יידיש.",
    "yo": "Dahun ni Yorùbá.",
    "zu": "Phendula ngesiZulu.",
}


def _parse_gemini_response(text: str) -> dict:
    """Parse Gemini JSON response, falling back gracefully if not valid JSON."""
    cleaned = text.strip()
    cleaned = re.sub(r"^```(?:json)?\s*", "", cleaned)
    cleaned = re.sub(r"\s*```$", "", cleaned).strip()
    try:
        data = json.loads(cleaned)
        return {
            "summary": data.get("summary", text),
            "is_ai_generated": data.get("is_ai_generated", "uncertain"),
            "is_deepfake": data.get("is_deepfake", "uncertain"),
            "ai_confidence": data.get("ai_confidence", "low"),
            "ai_reason": data.get("ai_reason", ""),
        }
    except Exception:
        return {
            "summary": text,
            "is_ai_generated": "uncertain",
            "is_deepfake": "uncertain",
            "ai_confidence": "low",
            "ai_reason": "",
        }


_PROCESSING_TIMEOUT_SECONDS = 120  # abort if Gemini hasn't finished processing after 2 min


def analyze_video(video_path: str, language: str = "en", on_progress=None) -> dict:
    """
    Upload video to Gemini, return structured result with summary + AI detection.
    on_progress(step): optional callback to push progress labels to the frontend.
    """
    api_key = os.environ.get("GOOGLE_API_KEY", "")
    if not api_key:
        raise EnvironmentError("GOOGLE_API_KEY not set")

    client = genai.Client(api_key=api_key)
    video_file = None

    try:
        if on_progress:
            on_progress("uploading")

        with open(video_path, "rb") as f:
            video_file = client.files.upload(
                file=f,
                config=types.UploadFileConfig(mime_type="video/mp4"),
            )

        if on_progress:
            on_progress("processing")

        # Poll until ACTIVE, with a hard timeout to prevent worker stall
        waited = 0
        while video_file.state.name == "PROCESSING":
            if waited >= _PROCESSING_TIMEOUT_SECONDS:
                raise RuntimeError("Gemini video processing timed out.")
            time.sleep(2)
            waited += 2
            video_file = client.files.get(name=video_file.name)

        if video_file.state.name != "ACTIVE":
            raise RuntimeError(f"Gemini video processing failed: {video_file.state.name}")

        lang_instruction = LANG_PROMPTS.get(language, f"Reply in {language}.")
        prompt = "\n".join([
            f"Watch this short video completely. {lang_instruction}",
            "",
            "Return a JSON object with EXACTLY these fields (no markdown, no extra text):",
            "{",
            '  "summary": "<structured analysis in the target language with these 4 sections:',
            "1) All visuals and actions: Describe every visual element, scene change, person appearance, setting, camera movement, and on-screen action in detail.",
            "2) All visible text and subtitles: List every subtitle or text overlay with its timestamp, e.g. 0:00 - 0:03: 'spoken or displayed text' (spoken by / overlay).",
            "3) Core theme: Explain the main message, joke, or purpose. If it is a meme or humorous, explain the joke.",
            '4) Overall impression: Summarize the tone, quality, target audience, and what makes this video effective or notable.>",',
            '  "is_ai_generated": "<yes | no | uncertain>",',
            '  "is_deepfake": "<yes | no | uncertain>",',
            '  "ai_confidence": "<high | medium | low>",',
            # ai_reason now follows the selected language so users see it in their language
            '  "ai_reason": "<one sentence explanation of the AI/deepfake assessment in the target language>"',
            "}",
        ])

        last_err = None
        for model_name in GEMINI_MODELS:
            try:
                if on_progress:
                    on_progress("analyzing")
                response = client.models.generate_content(
                    model=model_name,
                    contents=[video_file, prompt],
                )
                return _parse_gemini_response(response.text)
            except Exception as e:
                last_err = str(e)

        raise RuntimeError(f"All Gemini models failed: {last_err}")

    finally:
        # Always delete the uploaded file to avoid quota accumulation
        if video_file is not None:
            try:
                client.files.delete(name=video_file.name)
            except Exception:
                pass  # Best-effort cleanup — don't mask the real error
