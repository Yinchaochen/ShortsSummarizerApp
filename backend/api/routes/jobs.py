from fastapi import APIRouter, HTTPException
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
        return {"state": "error", "detail": str(result.info)}

    return {"state": result.state.lower()}
