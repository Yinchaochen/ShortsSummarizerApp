from fastapi import APIRouter, Depends
from pydantic import BaseModel, field_validator
from api.middleware.auth import get_current_user, check_usage
from api.errors import AppError
from workers.tasks import summarize_video

router = APIRouter()

SUPPORTED_PLATFORMS = ("tiktok.com", "youtube.com", "youtu.be", "instagram.com")


class SummarizeRequest(BaseModel):
    url: str
    language: str = "en"

    @field_validator("url")
    @classmethod
    def validate_url(cls, v: str) -> str:
        v = v.strip()
        if not v.startswith("https://"):
            raise ValueError("URL must start with https://")
        if not any(p in v for p in SUPPORTED_PLATFORMS):
            raise AppError(
                "UNSUPPORTED_PLATFORM",
                "Only TikTok, YouTube, and Instagram links are supported.",
                status=422,
            )
        return v


@router.post("/summarize")
def submit_summarize(body: SummarizeRequest, user: dict = Depends(get_current_user)):
    check_usage(user["id"])
    task = summarize_video.delay(body.url, body.language, user["id"])
    return {"job_id": task.id}
