from fastapi import Header, HTTPException
from services.supabase_client import get_client

FREE_LIMIT = 3


def get_current_user(authorization: str = Header(None)) -> dict:
    """Validate Supabase JWT and return user dict."""
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Missing or invalid token")

    token = authorization.removeprefix("Bearer ")
    try:
        response = get_client().auth.get_user(token)
        return {"id": response.user.id, "email": response.user.email}
    except Exception:
        raise HTTPException(status_code=401, detail="Invalid or expired token")


def check_usage(user_id: str) -> int:
    """Return remaining free uses. Raise 403 if limit reached."""
    client = get_client()

    row = client.table("usage").select("free_count").eq("user_id", user_id).execute()

    if not row.data:
        # First time user — create usage row
        client.table("usage").insert({"user_id": user_id, "free_count": 0}).execute()
        return FREE_LIMIT

    used = row.data[0]["free_count"]
    remaining = FREE_LIMIT - used

    if remaining <= 0:
        raise HTTPException(
            status_code=403,
            detail=f"Free limit of {FREE_LIMIT} reached. Please subscribe to continue."
        )

    return remaining


def increment_usage(user_id: str):
    """Increment free_count by 1 after a successful summary."""
    client = get_client()
    row = client.table("usage").select("free_count").eq("user_id", user_id).execute()

    if row.data:
        new_count = row.data[0]["free_count"] + 1
        client.table("usage").update({"free_count": new_count}).eq("user_id", user_id).execute()
    else:
        client.table("usage").insert({"user_id": user_id, "free_count": 1}).execute()
