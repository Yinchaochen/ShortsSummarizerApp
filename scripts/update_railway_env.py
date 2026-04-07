#!/usr/bin/env python3
"""
Update YOUTUBE_COOKIES environment variable in Railway via GraphQL API.

Required env vars:
  RAILWAY_TOKEN       - Railway API token (from Account Settings > Tokens)
  RAILWAY_PROJECT_ID  - Project ID (from railway.app project URL or CLI)
  RAILWAY_ENV_ID      - Environment ID (production environment)
  RAILWAY_SERVICE_ID  - Service ID of the Celery worker service
  COOKIE_OUTPUT       - Path to Netscape cookie file (default: /tmp/yt_cookies.txt)
"""
import os
import sys
import requests

RAILWAY_TOKEN = os.environ["RAILWAY_TOKEN"]
RAILWAY_PROJECT_ID = os.environ["RAILWAY_PROJECT_ID"]
RAILWAY_ENV_ID = os.environ["RAILWAY_ENV_ID"]
RAILWAY_SERVICE_ID = os.environ["RAILWAY_SERVICE_ID"]
COOKIE_FILE = os.environ.get("COOKIE_OUTPUT", "/tmp/yt_cookies.txt")

API_URL = "https://backboard.railway.app/graphql/v2"

MUTATION = """
mutation VariableUpsert($input: VariableUpsertInput!) {
  variableUpsert(input: $input)
}
"""


def upsert_variable(name: str, value: str) -> bool:
    resp = requests.post(
        API_URL,
        headers={
            "Authorization": f"Bearer {RAILWAY_TOKEN}",
            "Content-Type": "application/json",
        },
        json={
            "query": MUTATION,
            "variables": {
                "input": {
                    "projectId": RAILWAY_PROJECT_ID,
                    "environmentId": RAILWAY_ENV_ID,
                    "serviceId": RAILWAY_SERVICE_ID,
                    "name": name,
                    "value": value,
                }
            },
        },
        timeout=30,
    )

    if not resp.ok:
        print(f"[ERROR] HTTP {resp.status_code}: {resp.text}", file=sys.stderr)
        return False

    data = resp.json()
    if data.get("errors"):
        print(f"[ERROR] GraphQL errors: {data['errors']}", file=sys.stderr)
        return False

    return True


def main():
    if not os.path.exists(COOKIE_FILE):
        print(f"[ERROR] Cookie file not found: {COOKIE_FILE}", file=sys.stderr)
        sys.exit(1)

    with open(COOKIE_FILE, encoding="utf-8") as f:
        cookie_value = f.read()

    if len(cookie_value.strip()) < 100:
        print("[ERROR] Cookie file looks empty or too short.", file=sys.stderr)
        sys.exit(1)

    print(f"Updating YOUTUBE_COOKIES in Railway (service: {RAILWAY_SERVICE_ID})...")
    if upsert_variable("YOUTUBE_COOKIES", cookie_value):
        print("Done. Railway will redeploy automatically.")
    else:
        print("[ERROR] Failed to update Railway env var.", file=sys.stderr)
        sys.exit(1)


main()
