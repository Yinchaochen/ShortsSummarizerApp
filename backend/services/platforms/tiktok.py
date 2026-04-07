import yt_dlp
from .base import BasePlatform


class TikTokPlatform(BasePlatform):
    """TikTok 视频下载（yt-dlp Python 模块，无需登录）。"""

    def download(self, url: str, output_path: str) -> bool:
        ydl_opts = {
            "format": "mp4/best",
            "outtmpl": output_path,
            "quiet": True,
            "no_playlist": True,
        }
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            ydl.download([url])
        return True
