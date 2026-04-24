from fastapi import APIRouter, Query, HTTPException, Depends
from pydantic import BaseModel, field_validator

from api.errors import AppError
from api.middleware.auth import get_current_user
from services.captions import extract_captions
from services.platforms.base import PlatformAccessError
from services.transcript import build_transcript

router = APIRouter()

SUPPORTED_PLATFORMS = (
    "tiktok.com",
    "youtube.com",
    "youtu.be",
    "instagram.com",
    "bilibili.com",
    "b23.tv",
    "xiaohongshu.com",
    "xhslink.com",
)


class TranscriptRequest(BaseModel):
    url: str
    language: str = "en"

    @field_validator("url")
    @classmethod
    def validate_url(cls, v: str) -> str:
        v = v.strip()
        if not v.startswith(("https://", "http://")):
            raise ValueError("URL must start with http:// or https://")
        if not any(platform in v for platform in SUPPORTED_PLATFORMS):
            raise AppError(
                "UNSUPPORTED_PLATFORM",
                "Only TikTok, YouTube, Instagram, Bilibili, and Xiaohongshu links are supported.",
                status=422,
            )
        return v


@router.get("/captions")
async def get_captions(
    url: str = Query(..., description="Video URL (TikTok, YouTube, Instagram, Bilibili, etc.)"),
):
    """
    Extract timed captions from a video URL.

    Returns a list of caption segments:
        [{"start": float, "end": float, "text": str, "x": float|null, "y": float|null}]

    "x" and "y" are relative screen coords (0.0–1.0) for TikTok creator text stickers.
    Speech captions have x=null, y=null.

    HTTP 404 — no captions found (video has no auto-captions).
    HTTP 502 — yt-dlp extraction failure (private/deleted video, network error, access blocked).
    """
    try:
        segments = extract_captions(url)
        return {"segments": segments, "count": len(segments)}
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except PlatformAccessError as e:
        raise HTTPException(
            status_code=502,
            detail={"code": e.code, "message": e.message},
        )
    except RuntimeError as e:
        raise HTTPException(status_code=502, detail=str(e))


@router.post("/transcript")
async def get_transcript(
    body: TranscriptRequest,
    _: dict = Depends(get_current_user),
):
    try:
        return build_transcript(body.url, target_language=body.language)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except PlatformAccessError as e:
        raise HTTPException(
            status_code=502,
            detail={"code": e.code, "message": e.message},
        )
    except (RuntimeError, EnvironmentError) as e:
        raise HTTPException(status_code=502, detail=str(e))
