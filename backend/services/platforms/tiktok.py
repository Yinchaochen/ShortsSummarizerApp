import subprocess
from .base import BasePlatform


class TikTokPlatform(BasePlatform):
    """TikTok 视频下载（yt-dlp，无需登录）。"""

    def download(self, url: str, output_path: str) -> bool:
        result = subprocess.run(
            [
                "yt-dlp",
                "--no-playlist",
                "-f", "mp4/best",
                url,
                "-o", output_path,
                "-q",
            ],
            capture_output=True,
            text=True,
        )
        if result.returncode != 0:
            raise RuntimeError(f"TikTok download failed: {result.stderr[:300]}")
        return True
