import os
import tempfile
import yt_dlp
from .base import BasePlatform


class YouTubePlatform(BasePlatform):
    """YouTube / YouTube Shorts video downloader (yt-dlp)."""

    def download(self, url: str, output_path: str) -> bool:
        ydl_opts = {
            "format": "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best",
            "outtmpl": output_path,
            "quiet": True,
            "no_playlist": True,
        }

        cookies_content = os.environ.get("YOUTUBE_COOKIES", "")
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
