from .base import BasePlatform
from .tiktok import TikTokPlatform
from .instagram import InstagramPlatform
from .youtube import YouTubePlatform

PLATFORM_MAP = {
    "tiktok": TikTokPlatform,
    "instagram": InstagramPlatform,
    "youtube": YouTubePlatform,
}


def get_downloader(url: str) -> BasePlatform:
    platform = BasePlatform.detect(url)
    cls = PLATFORM_MAP.get(platform)
    if cls is None:
        raise ValueError(f"Unsupported platform: {platform}")
    return cls()
