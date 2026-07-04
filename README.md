# Uchia / Shorts Summarizer

AI-powered short-video summarizer for TikTok, YouTube Shorts, Instagram Reels, Bilibili, and RedNote/Xiaohongshu. Paste a supported short-video link and get a structured timestamped breakdown, subtitle transcript, and AI/deepfake signal. The repo also includes an Android live screen-translation overlay and a Claude Code skill wrapper around the deployed API.

Live app: [shorts-summarizer-app.vercel.app](https://shorts-summarizer-app.vercel.app)
Latest APK: [GitHub releases](https://github.com/Yinchaochen/ShortsSummarizerApp/releases/latest)

## Current Capabilities

- Summarize short videos with Gemini video analysis.
- Extract timed captions and build bilingual transcripts when source and target languages differ.
- Detect likely AI-generated or deepfake video content in the Gemini analysis result.
- Download from platform-specific yt-dlp adapters with cookie support where needed.
- Enforce authenticated usage through Supabase JWTs and daily free quota tracking.
- Run a native Android live-translation overlay using MediaProjection, OCR, ML Kit translation, and positioned subtitles.
- Expose the deployed API as an installable Claude Code skill in `skill/SKILL.md`.
- Keep a graphify project knowledge graph in `graphify-out/`.

## Supported Platforms

| Platform | URL examples | Notes |
|---|---|---|
| TikTok | `tiktok.com`, `vm.tiktok.com` | Captions depend on source availability. |
| YouTube Shorts | `youtube.com/shorts`, `youtu.be` | Uses yt-dlp captions and video download. |
| Instagram Reels/Posts | `instagram.com/reel`, `instagram.com/p` | May require cookies for restricted content. |
| Bilibili | `bilibili.com/video`, `b23.tv` | Supports logged-in cookie injection. |
| RedNote / Xiaohongshu | `xiaohongshu.com`, `xhslink.com` | Code uses `xiaohongshu` naming internally. |

## Architecture

```text
Expo app (React Native + Web)
  - app screens, Supabase auth, usage display, result/transcript UI
  - deployed on Vercel for web

FastAPI backend
  - /api/v1/summarize starts a Celery video-analysis job
  - /api/v1/job/{job_id} polls job state/result
  - /api/v1/captions extracts raw timed caption segments
  - /api/v1/transcript returns formatted transcript output
  - /api/v1/usage returns free quota state

Worker layer
  - Celery + Redis
  - yt-dlp platform downloaders
  - video compression/preparation
  - Gemini 2.5 Flash analysis

Data and auth
  - Supabase JWT validation
  - Supabase usage tracking and summary persistence

Android live translation
  - MediaProjection capture
  - frame diff sampling
  - ML Kit OCR and translation
  - native overlay rendering
```

## Repository Layout

| Path | Purpose |
|---|---|
| `app/` | Expo React Native app and Android project. |
| `backend/` | FastAPI API, Celery worker, platform services, tests, Railway config. |
| `backend/services/platforms/` | Platform adapters built around yt-dlp. |
| `backend/services/captions.py` | Caption extraction and parsing pipeline. |
| `backend/services/transcript.py` | Transcript formatting and optional translation. |
| `skill/SKILL.md` | Claude Code skill that calls the deployed API. |
| `scripts/` | Maintenance and one-off utility scripts. |
| `ai-study-pack/` | Generated recommender-system study material. |
| `graphify-out/` | Project knowledge graph outputs. |
| `AGENTS.md` | Agent instructions for this repository. |

## Quick Start

### Backend

```bash
cd backend
python -m venv .venv
# Windows PowerShell: .venv\Scripts\Activate.ps1
# macOS/Linux: source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn main:app --reload
```

Fill `backend/.env` before running real jobs:

```env
GOOGLE_API_KEY=your_gemini_api_key
REDIS_URL=redis://localhost:6379/0
SUPABASE_URL=your_supabase_url
SUPABASE_SERVICE_KEY=your_supabase_service_key
BILIBILI_COOKIES=optional_exported_bilibili_cookies
XIAOHONGSHU_COOKIES=optional_exported_xiaohongshu_cookies
```

### Worker

```bash
cd backend
celery -A workers.tasks.celery_app worker --loglevel=info --concurrency=2
```

### Frontend

```bash
cd app
npm install
npx expo start
```

For web:

```bash
cd app
npm run web
```

### Android APK

```bash
cd app/android
./gradlew assembleDebug
```

The debug APK is written under `app/android/app/build/outputs/apk/debug/`.

## API

All new integrations should use `/api/v1`. Legacy `/api` aliases still exist for older app installs.

| Method | Path | Auth | Purpose |
|---|---|---|---|
| `POST` | `/api/v1/summarize` | Supabase JWT | Start video summary job. |
| `GET` | `/api/v1/job/{job_id}` | Supabase JWT | Poll Celery job status/result. |
| `GET` | `/api/v1/captions?url=...` | None | Extract raw caption segments. |
| `POST` | `/api/v1/transcript` | Supabase JWT | Build formatted transcript output. |
| `GET` | `/api/v1/usage` | Supabase JWT | Read free quota state. |
| `GET` | `/health` | None | Health check. |

Example:

```bash
curl -X POST "http://localhost:8000/api/v1/summarize" \
  -H "Authorization: Bearer $UCHIA_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://youtube.com/shorts/example","language":"en"}'
```

## Environment Variables

| Variable | Used by | Purpose |
|---|---|---|
| `GOOGLE_API_KEY` | backend, scripts | Gemini API access. |
| `REDIS_URL` | backend worker | Celery broker/result backend. |
| `SUPABASE_URL` | backend, app | Supabase project URL. |
| `SUPABASE_ANON_KEY` | app | Supabase public client key. |
| `SUPABASE_SERVICE_KEY` | backend | Supabase admin/service access. |
| `YOUTUBE_COOKIES` | worker | Optional YouTube cookie string. |
| `INSTAGRAM_COOKIES` | worker | Optional Instagram cookie string. |
| `BILIBILI_COOKIES` | worker | Optional Bilibili cookie string. |
| `XIAOHONGSHU_COOKIES` | worker | Optional RedNote/Xiaohongshu cookie string. |
| `UCHIA_TOKEN` | skill/scripts | Supabase JWT for API calls. |

Never commit exported cookie files, `.env`, transcripts, meeting audio, or `.tmp/` outputs.

## Useful Commands

| Command | Purpose |
|---|---|
| `python -m unittest discover backend/tests` | Run backend tests. |
| `python -m py_compile scripts/build_recommender_ai_study_pack.py scripts/describe_ui.py scripts/summarize_one.py` | Syntax-check utility scripts. |
| `python -c "from graphify.watch import _rebuild_code; from pathlib import Path; _rebuild_code(Path('.'))"` | Rebuild graphify outputs after code changes. |
| `npm run web` | Start the Expo web app. |
| `npm run android` | Run the native Android app through Expo. |

Note: on this Windows workspace, `python3` may point to the Microsoft Store alias. Use `python` if `python3` is unavailable.

## Utility Scripts

| Script | Purpose |
|---|---|
| `scripts/export_cookies.py` | Convert browser storage state to Netscape cookie format for YouTube/Google. |
| `scripts/update_railway_env.py` | Push exported cookies into Railway environment variables. |
| `scripts/generate_translations.py` | Generate UI translations with Gemini. |
| `scripts/summarize_one.py` | One-off Bilibili download and Gemini summary test. |
| `scripts/describe_ui.py` | One-off UI-description analysis for a supported video URL. |
| `scripts/build_recommender_ai_study_pack.py` | Generate the `ai-study-pack/` material. |

## Claude Code Skill

This repo ships a skill at `skill/SKILL.md`. Install it into Claude Code to analyze supported short-video URLs through the deployed API.

```bash
mkdir -p ~/.claude/skills/shorts-summarizer
cp skill/SKILL.md ~/.claude/skills/shorts-summarizer/SKILL.md
```

Windows PowerShell:

```powershell
$dest = "$env:USERPROFILE\.claude\skills\shorts-summarizer"
New-Item -ItemType Directory -Force -Path $dest | Out-Null
Copy-Item skill\SKILL.md "$dest\SKILL.md"
```

Then set `UCHIA_TOKEN` to a Supabase access token from the web app login session.

## Graphify

This repository keeps a graphify knowledge graph in `graphify-out/`.

Rules for agents and maintainers:

- Read `graphify-out/GRAPH_REPORT.md` before answering project questions.
- If `graphify-out/wiki/index.md` exists, navigate the wiki instead of raw files.
- After modifying code files, rebuild graphify:

```bash
python -c "from graphify.watch import _rebuild_code; from pathlib import Path; _rebuild_code(Path('.'))"
```

Local scratch output, cookies, meeting recordings, and graphify caches are ignored by `.gitignore` and `.graphifyignore`.

## Testing Notes

Backend unit tests currently cover:

- platform detection and request validation
- Bilibili caption parsing
- transcript formatting
- video preparation/compression decisions

Run:

```bash
python -m unittest discover backend/tests
```

## Deployment Notes

- Frontend: Vercel.
- Backend web service: Railway Dockerfile deployment.
- Worker: Railway celery worker with Redis.
- Web routes should use `/api/v1`.
- `backend/Dockerfile` owns the default web `CMD`; `backend/railway.worker.toml` owns the worker start command.

## License

MIT
