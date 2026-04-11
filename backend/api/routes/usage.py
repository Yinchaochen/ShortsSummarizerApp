from fastapi import APIRouter, Depends
from datetime import datetime, timezone, timedelta
from api.middleware.auth import get_current_user, _get_or_create_usage, FREE_LIMIT, RESET_HOURS
from services.supabase_client import get_client

router = APIRouter()


@router.get("/usage")
def get_usage(user: dict = Depends(get_current_user)):
    client = get_client()
    data = _get_or_create_usage(client, user["id"])
    used = data["free_count"]
    last_reset_raw = data.get("last_reset_at")

    resets_at = None
    if last_reset_raw:
        last_reset = last_reset_raw if isinstance(last_reset_raw, datetime) else \
            datetime.fromisoformat(str(last_reset_raw).replace("Z", "+00:00"))
        resets_at = (last_reset + timedelta(hours=RESET_HOURS)).isoformat()

    return {
        "free_limit": FREE_LIMIT,
        "used": used,
        "remaining": max(0, FREE_LIMIT - used),
        "resets_at": resets_at,
    }
