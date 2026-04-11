from abc import ABC, abstractmethod


class BasePlatform(ABC):
    """Abstract base for all platform downloaders. Add a new platform by subclassing and implementing download()."""

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
        return "unknown"
