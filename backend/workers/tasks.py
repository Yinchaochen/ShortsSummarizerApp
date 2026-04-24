import os
import sys
import uuid
import tempfile
import yt_dlp
from pathlib import Path
from dotenv import load_dotenv

# Ensure backend/ is in Python path regardless of where Celery is launched from
sys.path.insert(0, str(Path(__file__).parent.parent))
from celery import Celery
from services.platforms import get_downloader
from services.gemini import analyze_video
from services.platforms.base import (
    BILIBILI_ACCESS_BLOCKED_CODE,
    BILIBILI_ACCESS_BLOCKED_MESSAGE,
    BasePlatform,
    PlatformAccessError,
)

MAX_DURATION_SECONDS = 600  # 10 minutes
MAX_FILE_SIZE_MB = 300      # Secondary guard: ~300 MB covers ~10 min at typical bitrate


def _build_probe_opts(url: str) -> tuple[dict, str | None]:
    opts = {"quiet": True, "no_warnings": True, "skip_download": True}
    cookie_file = None

    if BasePlatform.detect(url) == "bilibili":
        opts["http_headers"] = dict(BasePlatform.BILIBILI_HEADERS)
        cookie_file = BasePlatform.add_cookie_file(opts, "BILIBILI_COOKIES")

    return opts, cookie_file


def _get_video_duration(url: str) -> float | None:
    """Fetch video duration in seconds without downloading.
    Returns None if the platform does not expose duration.
    Raises yt_dlp.utils.DownloadError on hard failures (blocked, not found).
    """
    opts, cookie_file = _build_probe_opts(url)

    try:
        with yt_dlp.YoutubeDL(opts) as ydl:
            info = ydl.extract_info(url, download=False)
            if info is None:
                return None
            if info.get("_type") == "playlist":
                entries = info.get("entries") or []
                info = next(iter(entries), None)
                if info is None:
                    return None
            return info.get("duration")
    except yt_dlp.utils.DownloadError as exc:
        if BasePlatform.detect(url) == "bilibili" and BasePlatform.is_bilibili_access_blocked(exc):
            raise PlatformAccessError(
                BILIBILI_ACCESS_BLOCKED_CODE,
                BILIBILI_ACCESS_BLOCKED_MESSAGE,
            ) from exc
        raise
    finally:
        BasePlatform.cleanup_cookie_file(cookie_file)


load_dotenv(Path(__file__).parent.parent / ".env")

REDIS_URL = os.environ.get("REDIS_URL", "redis://localhost:6379/0")

celery_app = Celery("shorts_summarizer", broker=REDIS_URL, backend=REDIS_URL)

celery_app.conf.update(
    task_serializer="json",
    result_serializer="json",
    accept_content=["json"],
    task_track_started=True,
    result_expires=86400,
    broker_connection_retry_on_startup=True,
)


@celery_app.task(bind=True)
def summarize_video(self, url: str, language: str = "en", user_id: str | None = None):
    """Download video, analyze with Gemini, save result to Supabase."""
    tmp_path = os.path.join(tempfile.gettempdir(), f"shorts_{uuid.uuid4().hex}.mp4")

    try:
        # URL validation — unsupported platforms are caught here too (belt-and-suspenders)
        platform = BasePlatform.detect(url)
        if platform == "unknown":
            raise ValueError("UNSUPPORTED_PLATFORM")

        try:
            duration = _get_video_duration(url)
        except PlatformAccessError as exc:
            raise ValueError(exc.code) from exc
        except yt_dlp.utils.DownloadError:
            duration = None

        if duration is not None and duration > MAX_DURATION_SECONDS:
            raise ValueError("VIDEO_TOO_LONG")

        self.update_state(state="PROGRESS", meta={"step": "downloading"})
        downloader = get_downloader(url)
        try:
            downloader.download(url, tmp_path)
        except PlatformAccessError as exc:
            raise ValueError(exc.code) from exc

        file_size_mb = os.path.getsize(tmp_path) / (1024 * 1024)
        if file_size_mb > MAX_FILE_SIZE_MB:
            raise ValueError("VIDEO_TOO_LONG")

        def on_progress(step: str):
            self.update_state(state="PROGRESS", meta={"step": step})

        result = analyze_video(tmp_path, language=language, on_progress=on_progress)

        if user_id:
            from services.supabase_client import get_client
            from api.middleware.auth import increment_usage
            get_client().table("summaries").insert({
                "user_id": user_id,
                "url": url,
                "platform": platform,
                "language": language,
                "result": result,
            }).execute()
            increment_usage(user_id)

        return {"status": "done", "result": result}

    finally:
        if os.path.exists(tmp_path):
            os.remove(tmp_path)
