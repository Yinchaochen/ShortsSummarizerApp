import os
import tempfile
from abc import ABC, abstractmethod


BILIBILI_ACCESS_BLOCKED_CODE = "BILIBILI_ACCESS_BLOCKED"
BILIBILI_ACCESS_BLOCKED_MESSAGE = (
    "Bilibili blocked this request; refresh BILIBILI_COOKIES on the worker."
)


class PlatformAccessError(RuntimeError):
    def __init__(self, code: str, message: str):
        super().__init__(message)
        self.code = code
        self.message = message


class BasePlatform(ABC):
    """Abstract base for all platform downloaders. Add a new platform by subclassing and implementing download()."""

    BROWSER_HEADERS = {
        "User-Agent": (
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/124.0.0.0 Safari/537.36"
        ),
        "Accept-Language": "en-US,en;q=0.9",
    }

    BILIBILI_HEADERS = {
        **BROWSER_HEADERS,
        "Referer": "https://www.bilibili.com/",
        "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
    }

    @abstractmethod
    def download(self, url: str, output_path: str) -> bool:
        """Download the video to output_path. Returns True on success."""
        ...

    @staticmethod
    def detect(url: str) -> str:
        """Return the platform name for a given URL."""
        if "tiktok.com" in url:
            return "tiktok"
        if "instagram.com" in url:
            return "instagram"
        if "youtube.com" in url or "youtu.be" in url:
            return "youtube"
        if "bilibili.com" in url or "b23.tv" in url:
            return "bilibili"
        return "unknown"

    @staticmethod
    def add_cookie_file(ydl_opts: dict, env_var: str) -> str | None:
        content = os.environ.get(env_var, "").strip()
        if not content:
            return None

        tmp = tempfile.NamedTemporaryFile(
            mode="w", suffix=".txt", delete=False, encoding="utf-8"
        )
        tmp.write(content.replace("\r\n", "\n").replace("\r", "\n"))
        tmp.flush()
        tmp.close()
        ydl_opts["cookiefile"] = tmp.name
        return tmp.name

    @staticmethod
    def cleanup_cookie_file(cookie_file: str | None) -> None:
        if cookie_file and os.path.exists(cookie_file):
            os.remove(cookie_file)

    @staticmethod
    def is_bilibili_access_blocked(error: Exception | str) -> bool:
        message = str(error).lower()
        markers = (
            "http error 412",
            "precondition failed",
            "request is blocked by server",
            "subtitles are only available when logged in",
            "login required",
            "need_login",
        )
        return any(marker in message for marker in markers)
