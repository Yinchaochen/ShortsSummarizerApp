# coding: utf-8
import sys
import time
import requests
from supabase import create_client
from dotenv import load_dotenv
from pathlib import Path
import os

load_dotenv(Path(__file__).parent / ".env")

SUPABASE_URL = os.environ["SUPABASE_URL"]
SUPABASE_ANON_KEY = os.environ.get("SUPABASE_ANON_KEY", os.environ.get("SUPABASE_SERVICE_KEY"))
API_BASE = "https://shortssummarizer.up.railway.app"

if len(sys.argv) < 4:
    print("Usage: python test_api.py <email> <password> <video_url>")
    sys.exit(1)

email = sys.argv[1]
password = sys.argv[2]
url_arg = sys.argv[3]

client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
res = client.auth.sign_in_with_password({"email": email, "password": password})
token = res.session.access_token
print(f"\n[OK] Logged in: {res.user.email}")

headers = {"Authorization": f"Bearer {token}"}

usage = requests.get(f"{API_BASE}/api/usage", headers=headers).json()
print(f"[Usage] used={usage['used']}/{usage['free_limit']}  remaining={usage['remaining']}")

resp = requests.post(f"{API_BASE}/api/summarize", json={"url": url_arg, "language": "zh"}, headers=headers)
if resp.status_code != 200:
    print(f"[ERR] Submit failed: {resp.status_code} {resp.text}")
    sys.exit(1)

job_id = resp.json()["job_id"]
print(f"[OK] Job submitted: {job_id}")
print("[Wait] Processing...")

while True:
    job = requests.get(f"{API_BASE}/api/job/{job_id}", headers=headers).json()
    state = job["state"]
    if state == "progress":
        print(f"  -> {job.get('label', state)}")
    elif state == "done":
        print(f"\n[DONE]\n\n{job['result']}")
        break
    elif state == "error":
        print(f"\n[ERR] {job.get('detail')}")
        break
    else:
        print(f"  -> {state}")
    time.sleep(3)
