# Uchia — Short Video Summarizer

AI-powered summarizer for TikTok, YouTube Shorts, and Instagram Reels. Paste a link, get a structured breakdown in seconds. Also ships a real-time screen translation overlay for Android.

**Live app → [uchia.io](https://shorts-summarizer-app.vercel.app)**
**Download APK → [Latest release](https://github.com/Yinchaochen/ShortsSummarizerApp/releases/latest)**

---

## What it does

- **Summarize any short video** — TikTok, YouTube Shorts, Instagram Reels
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
│  Platform abstraction: TikTok / YouTube / IG     │
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
| `REDIS_URL` | Railway | Celery broker |

## License

MIT
