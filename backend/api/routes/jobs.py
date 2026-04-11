from fastapi import APIRouter
from celery.result import AsyncResult
from workers.tasks import celery_app

router = APIRouter()

STEP_LABELS = {
    "downloading": "Downloading video...",
    "uploading":   "Uploading to Gemini...",
    "processing":  "Gemini processing video...",
    "analyzing":   "Analyzing content...",
}


@router.get("/job/{job_id}")
def get_job(job_id: str):
    result = AsyncResult(job_id, app=celery_app)

    if result.state == "PENDING":
        return {"state": "pending"}

    if result.state == "PROGRESS":
        step = (result.info or {}).get("step", "")
        return {"state": "progress", "step": step, "label": STEP_LABELS.get(step, step)}

    if result.state == "SUCCESS":
        return {"state": "done", "result": result.result.get("result")}

    if result.state == "FAILURE":
        exc = result.info
        # Structured errors raised with AppError-style detail dict
        if hasattr(exc, "detail") and isinstance(exc.detail, dict):
            return {"state": "error", "code": exc.detail.get("code", "ERROR"), "detail": exc.detail.get("message", str(exc))}
        # Plain ValueError (e.g. VIDEO_TOO_LONG) — keep backward compat
        detail = str(exc)
        return {"state": "error", "code": detail, "detail": detail}

    return {"state": result.state.lower()}
