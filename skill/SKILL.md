---
name: shorts-summarizer
description: Analyze short videos (Instagram Reels, TikTok, YouTube Shorts, Bilibili, Xiaohongshu) by calling the user's deployed Uchia / Shorts Summarizer API. Returns a chronological breakdown with timestamps, on-screen text, main message, tone, and AI-detection flags. Trigger when the user pastes a video URL from one of these platforms and asks to "analyze / summarize / extract / 分析 / 总结" the video, or wants to use the video as a UI/design reference. Skip when the user just shares the link as conversation context without asking for analysis.
---

# Shorts Summarizer (Uchia) skill

Calls the user's own Railway-deployed FastAPI backend at `https://shortssummarizer.up.railway.app` to summarize a short-form video. Source: [github.com/Yinchaochen/ShortsSummarizerApp](https://github.com/Yinchaochen/ShortsSummarizerApp). Local copy at `E:\Shorts Summarizer`.

## When to trigger

User pastes a URL from any supported platform AND asks for analysis / summary / breakdown / "what's in this video" / "use this as a reference":

| Platform | URL pattern |
|---|---|
| Instagram Reels | `instagram.com/reel/...`, `instagram.com/p/...` |
| TikTok | `tiktok.com/...`, `vm.tiktok.com/...` |
| YouTube Shorts | `youtube.com/shorts/...`, `youtu.be/...` |
| Bilibili | `bilibili.com/video/...`, `b23.tv/...` |
| Xiaohongshu | `xiaohongshu.com/explore/...`, `xhslink.com/...` |

Don't trigger for: long-form YouTube, Vimeo, generic web pages, or when the URL is just shared as conversation context without an analysis request.

## Auth — required

The API requires a Supabase JWT (Bearer token). Two ways to source it:

1. **`UCHIA_TOKEN` env var** — preferred for repeated calls. Check `echo $UCHIA_TOKEN` in Bash first.
2. **Ask the user** — if env var isn't set, prompt: "I need your Uchia/Shorts Summarizer token. Open https://shorts-summarizer-app.vercel.app, log in, then in browser DevTools → Application → Local Storage → find the `sb-*-auth-token` key, copy the `access_token` value, paste it here. Or set `UCHIA_TOKEN` env var so I don't have to ask next time."

Never echo the token in your reply. Pass it as a `Bearer` header.

## API flow

```
POST /api/v1/summarize        body: {"url": "...", "language": "en"}     → {"job_id": "..."}
GET  /api/v1/job/{job_id}     poll every 3-5s                            → state: pending|progress|done|error
```

`language` should match the user's reply language (en / zh / de / ...). If unclear, default `en`.

### Sample one-shot bash (run via Bash tool)

```bash
TOKEN="$UCHIA_TOKEN"   # or paste from user
URL="https://www.instagram.com/reel/DX_csG5TILA/"
LANG="zh"

JOB=$(curl -sS -X POST "https://shortssummarizer.up.railway.app/api/v1/summarize" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"url\":\"$URL\",\"language\":\"$LANG\"}" | python -c "import sys,json; print(json.load(sys.stdin)['job_id'])")

echo "job_id=$JOB"

# Poll. Gemini analysis runs ~30-90s for a typical 30-second reel.
for i in $(seq 1 60); do
  RESP=$(curl -sS "https://shortssummarizer.up.railway.app/api/v1/job/$JOB" -H "Authorization: Bearer $TOKEN")
  STATE=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin).get('state',''))")
  if [ "$STATE" = "done" ] || [ "$STATE" = "error" ]; then
    echo "$RESP" | python -m json.tool
    break
  fi
  STEP=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin).get('label',''))" 2>/dev/null)
  echo "[$i] $STATE $STEP"
  sleep 4
done
```

Use Bash tool's `run_in_background` for the polling loop and read output when done.

## Response schema

`done` → `{"state":"done", "result": { ... }}` where `result` is:

```json
{
  "summary": "0:00-0:05 — opens with X holding Y in frame...\n0:05-0:12 — cuts to ...\n[on-screen text: \"...\"]\nMain message: ...\nTone: warm, kawaii, ...",
  "is_ai_generated": "yes" | "no" | "uncertain",
  "is_deepfake": "yes" | "no" | "uncertain",
  "ai_confidence": "high" | "medium" | "low",
  "ai_reason": "..."
}
```

The `summary` is the meat — chronological timestamped breakdown with on-screen text and an editorial close (main message + tone). Use it directly for UI/design reference work, content briefs, study notes.

`error` → `{"state":"error", "code":"...", "detail":"..."}`. Common codes:
- `UNSUPPORTED_PLATFORM` — URL not in the supported list
- `VIDEO_TOO_LONG` — exceeds duration / file-size cap (≈ 5 min / 100 MB)
- `BILIBILI_ACCESS_BLOCKED` — backend cookies stale; tell user to refresh `BILIBILI_COOKIES` on the worker
- `UNAUTHORIZED` — token missing / expired

## Free tier

Logged-in user gets 15 summaries / 24h. After that the API returns 429.

## What to do with the result

For UI / design reference:
- Quote the relevant slices of `summary` (e.g. "0:08-0:12 — bottom navigation appears as floating pill...")
- Translate the timeline into a concrete spec list: layouts, colors mentioned, animations described, copy phrasing
- If the user wants a HTML/RN mockup, hand the summary to the design skill of choice (e.g. `huashu-design`) as input

Don't paraphrase the entire summary back to the user — they can read the JSON. Pick the parts that answer their actual question.

## Common pitfalls

- **Don't poll forever.** Cap at ~3 min total (60 × 4s). Beyond that it's almost certainly a backend stall — surface the last `step` to the user and stop.
- **Don't store the token in a file or commit.** Always read from env var or paste-once.
- **Instagram URLs with query strings** (`?igsh=...`) work fine — backend does `url.contains("instagram.com")`. Don't strip them unless yt-dlp complains.
- **Language flag affects the OUTPUT only**, not transcript extraction. So `language: "zh"` returns a Chinese summary even for English videos.

## Followup if the API is down

If backend returns 502 / 504 / connection-refused, the Railway service may be sleeping or failed:
- Check `https://shortssummarizer.up.railway.app/health` — should return `{"status":"ok"}`
- If down: tell the user, suggest they check Railway dashboard / restart the service. Don't retry blindly.
