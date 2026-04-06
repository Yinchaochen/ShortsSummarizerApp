import os
import time
from google import genai
from google.genai import types

GEMINI_MODELS = [
    "models/gemini-2.5-flash",
    "models/gemini-flash-latest",
]

LANG_PROMPTS = {
    "zh": "请用中文回复。",
    "en": "Reply in English.",
    "ja": "日本語で回答してください。",
    "ko": "한국어로 답변해 주세요.",
    "es": "Por favor responde en español.",
    "fr": "Veuillez répondre en français.",
}


def analyze_video(video_path: str, language: str = "en", on_progress=None) -> str:
    """
    上传视频到 Gemini 并返回详细总结。
    on_progress(msg): 可选回调，用于向前端推送进度。
    """
    api_key = os.environ.get("GOOGLE_API_KEY", "")
    if not api_key:
        raise EnvironmentError("GOOGLE_API_KEY not set")

    client = genai.Client(api_key=api_key)

    if on_progress:
        on_progress("uploading")

    with open(video_path, "rb") as f:
        video_file = client.files.upload(
            file=f,
            config=types.UploadFileConfig(mime_type="video/mp4"),
        )

    if on_progress:
        on_progress("processing")

    while video_file.state.name == "PROCESSING":
        time.sleep(2)
        video_file = client.files.get(name=video_file.name)

    if video_file.state.name != "ACTIVE":
        raise RuntimeError(f"Gemini video processing failed: {video_file.state.name}")

    lang_instruction = LANG_PROMPTS.get(language, f"Reply in {language}.")
    prompt = (
        f"Watch this short video completely. {lang_instruction}\n"
        "Describe in detail:\n"
        "1) All visuals and actions (including fast cuts)\n"
        "2) All visible text and subtitles\n"
        "3) Core theme (if humorous/meme, explain the joke)\n"
        "4) Overall impression\n"
        "Output the analysis directly — no opening remarks."
    )

    last_err = None
    for model_name in GEMINI_MODELS:
        try:
            if on_progress:
                on_progress("analyzing")
            response = client.models.generate_content(
                model=model_name,
                contents=[video_file, prompt],
            )
            return response.text
        except Exception as e:
            last_err = str(e)

    raise RuntimeError(f"All Gemini models failed: {last_err}")

    try:
        client.files.delete(name=video_file.name)
    except Exception:
        pass
