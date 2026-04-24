import yt_dlp

from .base import BasePlatform


class XiaoHongShuPlatform(BasePlatform):
    """Xiaohongshu / Rednote video downloader (yt-dlp)."""

    def download(self, url: str, output_path: str) -> bool:
        ydl_opts = {
            "format": "mp4/best[ext=mp4]/best",
            "outtmpl": output_path,
            "quiet": True,
            "noprogress": True,
            "no_playlist": True,
            "http_headers": dict(self.XIAOHONGSHU_HEADERS),
        }

        cookie_file = self.add_cookie_file(ydl_opts, "XIAOHONGSHU_COOKIES")

        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                ydl.download([url])
        finally:
            self.cleanup_cookie_file(cookie_file)

        return True
