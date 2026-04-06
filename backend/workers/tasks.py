import os
import sys
import uuid
import tempfile
from pathlib import Path
from dotenv import load_dotenv

# Ensure backend/ is in Python path regardless of where Celery is launched from
sys.path.insert(0, str(Path(__file__).parent.parent))
from celery import Celery
from services.platforms import get_downloader
from services.gemini import analyze_video
from services.platforms.base import BasePlatform

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
        self.update_state(state="PROGRESS", meta={"step": "downloading"})
        downloader = get_downloader(url)
        downloader.download(url, tmp_path)

        def on_progress(step: str):
            self.update_state(state="PROGRESS", meta={"step": step})

        result = analyze_video(tmp_path, language=language, on_progress=on_progress)

        # Save to Supabase and increment usage
        if user_id:
            from services.supabase_client import get_client
            from api.middleware.auth import increment_usage
            platform = BasePlatform.detect(url)
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
