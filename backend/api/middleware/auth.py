from fastapi import Header
from datetime import datetime, timezone, timedelta
from services.supabase_client import get_client
from api.errors import AppError

FREE_LIMIT = 15
RESET_HOURS = 24


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


def _get_or_create_usage(client, user_id: str) -> dict:
    """Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed."""
    now = datetime.now(timezone.utc)
    row = client.table("usage").select("free_count, last_reset_at").eq("user_id", user_id).execute()

    if not row.data:
        client.table("usage").insert({
            "user_id": user_id,
            "free_count": 0,
            "last_reset_at": now.isoformat(),
        }).execute()
        return {"free_count": 0, "last_reset_at": now}

    data = row.data[0]
    free_count = data["free_count"]
    last_reset_raw = data.get("last_reset_at")

    # Reset counter if the 24-hour window has elapsed
    if last_reset_raw:
        last_reset = datetime.fromisoformat(last_reset_raw.replace("Z", "+00:00"))
        if now - last_reset >= timedelta(hours=RESET_HOURS):
            client.table("usage").update({
                "free_count": 0,
                "last_reset_at": now.isoformat(),
            }).eq("user_id", user_id).execute()
            return {"free_count": 0, "last_reset_at": now}

    return {"free_count": free_count, "last_reset_at": last_reset_raw}


def check_usage(user_id: str) -> int:
    """Check usage and raise 403 if the free limit is reached. Returns remaining uses."""
    client = get_client()
    data = _get_or_create_usage(client, user_id)
    used = data["free_count"]
    if used >= FREE_LIMIT:
        raise AppError(
            "USAGE_LIMIT",
            f"Free limit of {FREE_LIMIT} summaries per {RESET_HOURS}h reached.",
            status=403,
        )
    return FREE_LIMIT - used


def increment_usage(user_id: str) -> None:
    """Increment free_count by 1 after a successful summary."""
    client = get_client()
    data = _get_or_create_usage(client, user_id)
    client.table("usage").update({
        "free_count": data["free_count"] + 1,
    }).eq("user_id", user_id).execute()
