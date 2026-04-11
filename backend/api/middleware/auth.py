from fastapi import Header
from services.supabase_client import get_client
from api.errors import AppError

FREE_LIMIT = 15


def get_current_user(authorization: str = Header(None)) -> dict:
    """Validate Supabase JWT and return user dict."""
    if not authorization or not authorization.startswith("Bearer "):
        raise AppError("UNAUTHORIZED", "Missing or invalid token", status=401)

    token = authorization.removeprefix("Bearer ")
    try:
        response = get_client().auth.get_user(token)
        return {"id": response.user.id, "email": response.user.email}
    except Exception:
        raise AppError("UNAUTHORIZED", "Invalid or expired token", status=401)


def check_usage(user_id: str) -> int:
    """Check usage and raise 403 if the free limit is reached. Returns remaining uses."""
    client = get_client()
    row = client.table("usage").select("free_count").eq("user_id", user_id).execute()

    if not row.data:
        client.table("usage").insert({"user_id": user_id, "free_count": 0}).execute()
        return FREE_LIMIT

    used = row.data[0]["free_count"]
    if used >= FREE_LIMIT:
        raise AppError(
            "USAGE_LIMIT",
            f"Free limit of {FREE_LIMIT} summaries reached. Please subscribe to continue.",
            status=403,
        )
    return FREE_LIMIT - used


def increment_usage(user_id: str) -> None:
    """Increment free_count by 1 after a successful summary."""
    client = get_client()
    row = client.table("usage").select("free_count").eq("user_id", user_id).execute()
    if row.data:
        new_count = row.data[0]["free_count"] + 1
        client.table("usage").update({"free_count": new_count}).eq("user_id", user_id).execute()
    else:
        client.table("usage").insert({"user_id": user_id, "free_count": 1}).execute()
