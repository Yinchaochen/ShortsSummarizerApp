from fastapi import APIRouter, Query, HTTPException

from services.captions import extract_captions

router = APIRouter()


@router.get("/captions")
async def get_captions(
    url: str = Query(..., description="Video URL (TikTok, YouTube, Instagram, etc.)"),
):
    """
    Extract timed captions from a video URL.

    Returns a list of caption segments:
        [{"start": float, "end": float, "text": str, "x": float|null, "y": float|null}]

    "x" and "y" are relative screen coords (0.0–1.0) for TikTok creator text stickers.
    Speech captions have x=null, y=null.

    HTTP 404 — no captions found (video has no auto-captions).
    HTTP 502 — yt-dlp extraction failure (private/deleted video, network error).
    """
    try:
        segments = extract_captions(url)
        return {"segments": segments, "count": len(segments)}
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except RuntimeError as e:
        raise HTTPException(status_code=502, detail=str(e))
