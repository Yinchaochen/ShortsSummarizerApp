"""One-off summarize: download a Bilibili video and analyze with Gemini.

Bypasses Celery/Redis/Supabase. Reads GOOGLE_API_KEY from backend/.env and
uses the local cookies file directly (not the BILIBILI_COOKIES env var).
"""

import json
import os
import sys
import tempfile
import uuid
from pathlib import Path

from dotenv import load_dotenv

ROOT = Path(__file__).resolve().parent.parent
BACKEND = ROOT / "backend"
sys.path.insert(0, str(BACKEND))

load_dotenv(BACKEND / ".env")

import yt_dlp  # noqa: E402
from services.platforms.base import BasePlatform  # noqa: E402
from services.gemini import analyze_video  # noqa: E402


def download(url: str, out_path: str, cookie_file: str) -> None:
    ydl_opts = {
        "format": "mp4/best[ext=mp4]/best",
        "outtmpl": out_path,
        "quiet": True,
        "no_playlist": True,
        "http_headers": dict(BasePlatform.BILIBILI_HEADERS),
        "cookiefile": cookie_file,
    }
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])


def main():
    url = sys.argv[1]
    language = sys.argv[2] if len(sys.argv) > 2 else "zh"
    cookie_file = str(ROOT / "www.bilibili.com_cookies.txt")

    tmp = os.path.join(tempfile.gettempdir(), f"shorts_{uuid.uuid4().hex}.mp4")
    print(f"[1/2] downloading -> {tmp}", flush=True)
    download(url, tmp, cookie_file)
    size_mb = os.path.getsize(tmp) / (1024 * 1024)
    print(f"     done, {size_mb:.1f} MB", flush=True)

    print("[2/2] analyzing with Gemini...", flush=True)
    try:
        result = analyze_video(tmp, language=language, on_progress=lambda s: print(f"     {s}", flush=True))
    finally:
        if os.path.exists(tmp):
            os.remove(tmp)

    out_path = ROOT / "scripts" / "last_summary.json"
    out_path.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"\nresult written to: {out_path}", flush=True)


if __name__ == "__main__":
    main()
