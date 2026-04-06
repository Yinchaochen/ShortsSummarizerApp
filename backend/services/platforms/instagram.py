# Phase 2 预留 — Instagram 服务账号模式
from .base import BasePlatform


class InstagramPlatform(BasePlatform):
    def download(self, url: str, output_path: str) -> bool:
        raise NotImplementedError("Instagram support coming in Phase 2")
