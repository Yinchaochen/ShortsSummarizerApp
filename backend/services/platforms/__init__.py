from .base import BasePlatform
from .bilibili import BilibiliPlatform
from .tiktok import TikTokPlatform
from .instagram import InstagramPlatform
from .xiaohongshu import XiaoHongShuPlatform
from .youtube import YouTubePlatform

PLATFORM_MAP = {
    "bilibili": BilibiliPlatform,
    "tiktok": TikTokPlatform,
    "instagram": InstagramPlatform,
    "xiaohongshu": XiaoHongShuPlatform,
    "youtube": YouTubePlatform,
}


def get_downloader(url: str) -> BasePlatform:
    platform = BasePlatform.detect(url)
    cls = PLATFORM_MAP.get(platform)
    if cls is None:
        raise ValueError(f"Unsupported platform: {platform}")
    return cls()
