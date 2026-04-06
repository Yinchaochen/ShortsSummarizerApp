from abc import ABC, abstractmethod


class BasePlatform(ABC):
    """所有平台下载器的抽象基类。新增平台只需继承此类并实现 download()。"""

    @abstractmethod
    def download(self, url: str, output_path: str) -> bool:
        """下载视频到 output_path，成功返回 True。"""
        ...

    @staticmethod
    def detect(url: str) -> str:
        """根据 URL 返回平台名称。"""
        if "tiktok.com" in url:
            return "tiktok"
        if "instagram.com" in url:
            return "instagram"
        if "youtube.com" in url or "youtu.be" in url:
            return "youtube"
        if "twitter.com" in url or "x.com" in url:
            return "twitter"
        return "unknown"
