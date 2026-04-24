import yt_dlp

from .base import (
    BILIBILI_ACCESS_BLOCKED_CODE,
    BILIBILI_ACCESS_BLOCKED_MESSAGE,
    BasePlatform,
    PlatformAccessError,
)


class BilibiliPlatform(BasePlatform):
    """Bilibili video downloader (yt-dlp + cookies when required)."""

    def download(self, url: str, output_path: str) -> bool:
        ydl_opts = {
            "format": "mp4/best[ext=mp4]/best",
            "outtmpl": output_path,
            "quiet": True,
            "no_playlist": True,
            "http_headers": dict(self.BILIBILI_HEADERS),
        }

        cookie_file = self.add_cookie_file(ydl_opts, "BILIBILI_COOKIES")

        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                ydl.download([url])
        except Exception as exc:
            if self.is_bilibili_access_blocked(exc):
                raise PlatformAccessError(
                    BILIBILI_ACCESS_BLOCKED_CODE,
                    BILIBILI_ACCESS_BLOCKED_MESSAGE,
                ) from exc
            raise
        finally:
            self.cleanup_cookie_file(cookie_file)

        return True
