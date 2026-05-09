# Uchia — Short Video Summarizer

AI-powered summarizer for TikTok, YouTube Shorts, Instagram Reels, Bilibili, and Xiaohongshu videos. Paste a link, get a structured breakdown in seconds. Also ships a real-time screen translation overlay for Android.

**Live app → [uchia.io](https://shorts-summarizer-app.vercel.app)**
**Download APK → [Latest release](https://github.com/Yinchaochen/ShortsSummarizerApp/releases/latest)**

---

## What it does

- **Summarize any short video** — TikTok, YouTube Shorts, Instagram Reels, Bilibili, Xiaohongshu
- **Extract full subtitles** — pull the full transcript from supported videos, with bilingual side-by-side output when the transcript language differs from the selected app language
- **Gemini 2.5 Flash analysis** — visuals, subtitles, humor/meme structure, overall impression
- **Real-time screen translation** — Android overlay that captures any app's screen, OCR-detects subtitles, and translates them live using ML Kit (fully on-device, no network needed)
- **Free tier** — 15 summaries included, no account required to start
- **Multi-platform** — Expo web app + native Android APK

## Architecture

```
┌─────────────────────────────────────────────────┐
│                   Expo App (TypeScript)          │
│         React Native + Web (Vercel)              │
└──────────────────────┬──────────────────────────┘
                       │ REST
┌──────────────────────▼──────────────────────────┐
│              FastAPI Backend (Python)            │
│         Railway — web + celeryworker + redis     │
│                                                  │
│  yt-dlp download → Gemini 2.5 Flash analysis    │
│  Platform abstraction: TikTok / YouTube / IG / XHS│
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│           Supabase (Auth + DB)                   │
│    users · summaries · usage quota tracking      │
└─────────────────────────────────────────────────┘

Android Live Translation (separate, on-device):
  MediaProjection → FrameDiffSampler → ML Kit OCR
  → ML Kit Translate → PositionalOverlayView
```

## Tech Stack

| Layer | Tech |
|---|---|
| Frontend | Expo (React Native + Web), TypeScript |
| Backend | FastAPI, Celery, Redis |
| AI | Gemini 2.5 Flash (video analysis) |
| Video download | yt-dlp |
| Auth / DB | Supabase |
| Android translation | ML Kit OCR + Translate, MediaProjection (Kotlin) |
| Deployment | Vercel (frontend), Railway (backend) |

## Platforms supported

| Platform | Status |
|---|---|
| TikTok | ✅ |
| YouTube Shorts | ✅ |
| Instagram Reels | ✅ |
| Bilibili | ✅ |
| RedNote | ✅ |

## Running locally

### Backend

```bash
cd backend
pip install -r requirements.txt
cp .env.example .env   # fill in GEMINI_API_KEY, SUPABASE_URL, SUPABASE_KEY
uvicorn main:app --reload
```

### Frontend

```bash
cd app
npm install
npx expo start
```

### Android APK (local build)

```bash
cd app/android
./gradlew assembleDebug
# APK → app/android/app/build/outputs/apk/debug/
```

## Environment variables

| Variable | Where | Purpose |
|---|---|---|
| `GEMINI_API_KEY` | backend | Gemini API access |
| `SUPABASE_URL` | backend + app | Supabase project URL |
| `SUPABASE_ANON_KEY` | app | Supabase public key |
| `SUPABASE_SERVICE_KEY` | backend | Supabase admin key |
| `YOUTUBE_COOKIES` | Railway celeryworker | Cookie string for age-gated videos |
| `INSTAGRAM_COOKIES` | Railway celeryworker | Cookie string for Instagram downloads |
| `BILIBILI_COOKIES` | Railway celeryworker | Cookie string for Bilibili videos that require a logged-in session |
| `XIAOHONGSHU_COOKIES` | Railway celeryworker | Optional cookie string for Xiaohongshu videos that require a logged-in session |
| `REDIS_URL` | Railway | Celery broker |

## Use as a Claude Code skill

This repo ships a `SKILL.md` at [`skill/SKILL.md`](skill/SKILL.md) so Claude Code (or any AI agent that follows the same `~/.claude/skills/<name>/SKILL.md` convention) can call the deployed Uchia API directly. After installing the skill, you can paste any supported short-video URL in chat and ask "analyze this reel" / "summarize 这个视频" / "use this as a UI reference" — the agent will hit the API, poll the job, and hand you back the structured breakdown.

### Install

```bash
# macOS / Linux
mkdir -p ~/.claude/skills/shorts-summarizer
cp skill/SKILL.md ~/.claude/skills/shorts-summarizer/SKILL.md
```

```powershell
# Windows (PowerShell)
$dest = "$env:USERPROFILE\.claude\skills\shorts-summarizer"
New-Item -ItemType Directory -Force -Path $dest | Out-Null
Copy-Item skill\SKILL.md "$dest\SKILL.md"
```

Restart your Claude Code session — the skill will appear in the available-skills list.

### Set the API token (one-time)

The deployed API requires a Supabase JWT. Grab yours once:

1. Open [shorts-summarizer-app.vercel.app](https://shorts-summarizer-app.vercel.app) and log in
2. DevTools → **Application** → **Local Storage** → key like `sb-xxxxxx-auth-token`
3. Copy the `access_token` value (long `eyJ...` string)
4. Persist as a user env var so the skill can read it without prompting:

```bash
# bash / zsh — add to ~/.zshrc or ~/.bashrc
export UCHIA_TOKEN="eyJ..."
```

```powershell
# Windows — persist for the current user
[System.Environment]::SetEnvironmentVariable("UCHIA_TOKEN", "eyJ...", "User")
# Restart your shell / Claude Code session so the variable is picked up
```

### Use

Paste a URL + intent. Examples that auto-trigger the skill:

- `https://www.instagram.com/reel/DX_csG5TILA/ — analyze this for UI reference`
- `https://www.tiktok.com/@user/video/123 总结一下`
- `https://youtube.com/shorts/abc what's happening in this video?`

Supported platforms: TikTok, YouTube Shorts, Instagram Reels, Bilibili, Xiaohongshu. Free tier = 15 summaries / 24h per logged-in user.

### Customize for your own deployment

The shipped `SKILL.md` points at `https://shortssummarizer.up.railway.app`. If you fork this repo and deploy your own backend, edit the API base URL inside `SKILL.md` (search for `shortssummarizer.up.railway.app`) before installing.

## License

MIT
