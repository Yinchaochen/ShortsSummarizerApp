from fastapi import APIRouter, Depends
from api.middleware.auth import get_current_user, check_usage, FREE_LIMIT
from services.supabase_client import get_client

router = APIRouter()


@router.get("/usage")
def get_usage(user: dict = Depends(get_current_user)):
    client = get_client()
    row = client.table("usage").select("free_count").eq("user_id", user["id"]).execute()
    used = row.data[0]["free_count"] if row.data else 0
    return {
        "free_limit": FREE_LIMIT,
        "used": used,
        "remaining": max(0, FREE_LIMIT - used),
    }
