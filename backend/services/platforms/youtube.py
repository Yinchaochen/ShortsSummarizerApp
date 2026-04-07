import os
import tempfile
import yt_dlp
from .base import BasePlatform


class YouTubePlatform(BasePlatform):
    """YouTube / YouTube Shorts video downloader (yt-dlp)."""

    def download(self, url: str, output_path: str) -> bool:
        ydl_opts = {
            "format": "best",
            "extractor_args": {"youtube": {"player_client": ["tv_embedded"]}},
            "outtmpl": output_path,
            "quiet": True,
            "no_playlist": True,
        }

        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            ydl.download([url])

        return True
