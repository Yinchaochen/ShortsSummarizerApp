from fastapi import APIRouter, Depends
from pydantic import BaseModel
from api.middleware.auth import get_current_user, check_usage
from workers.tasks import summarize_video

router = APIRouter()


class SummarizeRequest(BaseModel):
    url: str
    language: str = "en"


@router.post("/summarize")
def submit_summarize(body: SummarizeRequest, user: dict = Depends(get_current_user)):
    check_usage(user["id"])
    task = summarize_video.delay(body.url, body.language, user["id"])
    return {"job_id": task.id}
