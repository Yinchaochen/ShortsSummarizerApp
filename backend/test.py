import sys
import time
import requests

BASE = "http://127.0.0.1:8000"

url = sys.argv[1] if len(sys.argv) > 1 else input("TikTok URL: ").strip()

print("提交任务...")
r = requests.post(f"{BASE}/api/summarize", json={"url": url, "language": "zh"})
job_id = r.json()["job_id"]
print(f"Job ID: {job_id}")

print("等待结果", end="", flush=True)
while True:
    r = requests.get(f"{BASE}/api/job/{job_id}")
    data = r.json()
    state = data["state"]

    if state == "done":
        print("\n\n" + "="*40)
        print(data["result"])
        break
    elif state == "error":
        print(f"\n错误: {data['detail']}")
        break
    elif state == "progress":
        print(f"\r状态: {data['label']}", end="", flush=True)
    else:
        print(".", end="", flush=True)

    time.sleep(2)
