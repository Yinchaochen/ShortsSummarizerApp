import yt_dlp
from .base import BasePlatform


class TikTokPlatform(BasePlatform):
    """TikTok 视频下载（yt-dlp Python 模块，无需登录）。"""

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
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            ydl.download([url])
        return True
