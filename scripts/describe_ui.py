"""Download a video from any supported platform and ask Gemini to describe UI in extreme detail.

Bypasses Celery/Redis. Custom prompt focused on UI element extraction (not summary).
"""

import json
import os
import sys
import tempfile
import time
import uuid
from pathlib import Path

from dotenv import load_dotenv

ROOT = Path(__file__).resolve().parent.parent
BACKEND = ROOT / "backend"
sys.path.insert(0, str(BACKEND))

load_dotenv(BACKEND / ".env")

from google import genai  # noqa: E402
from google.genai import types  # noqa: E402
from services.platforms import get_downloader  # noqa: E402
from services.platforms.base import BasePlatform  # noqa: E402

UI_PROMPT = """请用中文详尽描述这段视频中出现的所有软件 UI 细节。这是一份给开发者用来"复刻这个 App"的逆向参考文档。

请按视频时间线逐段拆解，每段格式：
[时间戳] 屏幕上的 UI 元素（位置 / 颜色 / 文字 / 图标 / 状态）

必须捕捉的细节：
1. 应用整体架构 — 标签栏（Tab Bar）位置 / 标签数量 / 标签图标 + 文字 / 当前激活的标签
2. 顶部导航栏 — 标题文字 / 左上和右上的按钮（图标 + 含义）/ 是否有搜索框 / 状态栏内容
3. 列表 / 卡片 — 卡片布局、每张卡片包含的字段、字体大小层级、点赞/评论/分享等图标的样式
4. 按钮 — 主按钮 / 次按钮 / 浮动按钮的颜色 / 圆角 / 阴影 / 图标
5. 颜色方案 — 主色 / 强调色 / 背景色 / 文字色（用 hex 估算或自然语言描述如"warm orange"）
6. 字体特征 — 是否系统字体 / 中文字体观感 / 标题与正文的对比
7. 排版风格 — 圆角大小 / 卡片间距 / 内容密度 / iOS/Android/web 风格倾向
8. 动效 / 过渡 — 任何点击反馈、滑动、转场动画
9. 输入态 / 弹窗 / 表单 — 输入框样式、键盘类型、弹窗位置、按钮排布
10. 任何带文字的元素必须**精确转录原文**（中英德都直接抄）
11. 是否能看出技术栈线索（iOS 原生 / Android 原生 / RN / Flutter / Web）

最后用一段总结：
- 这个 App 整体是什么类型（社交 / 工具 / 内容 / 电商 / 教育...）
- 核心导航流是什么（主屏 → 哪些下钻？）
- UI 风格属于哪一类（Material / iOS / Duolingo 卡通 / Notion 极简 / 小红书暖白...）
- 任何让你印象深刻的特殊设计

不要编造看不见的内容。看不清就写"看不清"。但也不要漏掉任何屏幕上明确出现的元素。
"""


def main():
    url = sys.argv[1]
    cookie_file = None

    tmp = os.path.join(tempfile.gettempdir(), f"ui_{uuid.uuid4().hex}.mp4")
    print(f"[1/2] downloading -> {tmp}", flush=True)
    downloader = get_downloader(url)
    downloader.download(url, tmp)
    size_mb = os.path.getsize(tmp) / (1024 * 1024)
    print(f"     done, {size_mb:.1f} MB", flush=True)

    print("[2/2] analyzing UI with Gemini...", flush=True)
    api_key = os.environ["GOOGLE_API_KEY"]
    client = genai.Client(api_key=api_key)

    try:
        with open(tmp, "rb") as f:
            video_file = client.files.upload(
                file=f, config=types.UploadFileConfig(mime_type="video/mp4")
            )
        print("     uploaded, waiting for ACTIVE...", flush=True)
        waited = 0
        while video_file.state.name == "PROCESSING":
            if waited >= 180:
                raise RuntimeError("Gemini processing timed out")
            time.sleep(2)
            waited += 2
            video_file = client.files.get(name=video_file.name)
        if video_file.state.name != "ACTIVE":
            raise RuntimeError(f"Gemini failed: {video_file.state.name}")

        print("     analyzing...", flush=True)
        response = client.models.generate_content(
            model="models/gemini-2.5-flash",
            contents=[video_file, UI_PROMPT],
        )
        text = response.text

        out_path = ROOT / "scripts" / "last_ui_description.md"
        out_path.write_text(text, encoding="utf-8")
        print(f"\nresult written to: {out_path}", flush=True)

        try:
            client.files.delete(name=video_file.name)
        except Exception:
            pass
    finally:
        if os.path.exists(tmp):
            os.remove(tmp)


if __name__ == "__main__":
    main()
