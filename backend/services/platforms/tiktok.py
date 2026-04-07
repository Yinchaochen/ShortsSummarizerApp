import os
import tempfile
import yt_dlp
from .base import BasePlatform


class TikTokPlatform(BasePlatform):
    """TikTok video downloader (yt-dlp)."""

    def download(self, url: str, output_path: str) -> bool:
        ydl_opts = {
            "format": "mp4/best[ext=mp4]/best",
            "outtmpl": output_path,
            "quiet": True,
            "no_playlist": True,
            "http_headers": {
                "User-Agent": (
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    "AppleWebKit/537.36 (KHTML, like Gecko) "
                    "Chrome/124.0.0.0 Safari/537.36"
                ),
                "Referer": "https://www.tiktok.com/",
                "Accept-Language": "en-US,en;q=0.9",
            },
        }

        cookies_content = os.environ.get("TIKTOK_COOKIES", "")
        cookie_file = None
        if cookies_content:
            tmp = tempfile.NamedTemporaryFile(
                mode="w", suffix=".txt", delete=False, encoding="utf-8"
            )
            tmp.write(cookies_content)
            tmp.flush()
            cookie_file = tmp.name
            tmp.close()
            ydl_opts["cookiefile"] = cookie_file

        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                ydl.download([url])
        finally:
            if cookie_file and os.path.exists(cookie_file):
                os.remove(cookie_file)

        return True
