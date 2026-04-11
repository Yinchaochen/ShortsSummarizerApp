# Graph Report - .  (2026-04-11)

## Corpus Check
- Corpus is ~47,986 words - fits in a single context window. You may not need a graph.

## Summary
- 292 nodes · 343 edges · 28 communities detected
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 27 edges (avg confidence: 0.7)
- Token cost: 0 input · 0 output

## God Nodes (most connected - your core abstractions)
1. `Shorts Summarizer Project Plan` - 24 edges
2. `BubbleModule` - 20 edges
3. `ScreenCaptureService` - 14 edges
4. `SubtitleAccessibilityService` - 12 edges
5. `ScreenCaptureModule` - 12 edges
6. `BasePlatform` - 11 edges
7. `FastAPI Backend (Python)` - 9 edges
8. `AppError` - 8 edges
9. `AccessibilityServiceStrategy` - 7 edges
10. `ScreenCaptureStrategy` - 7 edges

## Surprising Connections (you probably didn't know these)
- `Validate Supabase JWT and return user dict.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `Check usage and raise 403 if the free limit is reached. Returns remaining uses.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `Increment free_count by 1 after a successful summary.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `Instagram Reels / Posts downloader (yt-dlp + cookie file).` --uses--> `BasePlatform`  [INFERRED]
  backend\services\platforms\instagram.py → backend\services\platforms\base.py
- `TikTok video downloader (yt-dlp).` --uses--> `BasePlatform`  [INFERRED]
  backend\services\platforms\tiktok.py → backend\services\platforms\base.py

## Communities

### Community 0 - "Frontend App Layer"
Cohesion: 0.07
Nodes (7): ApiError, getAuthHeader(), getUsage(), handleResponse(), submitSummarize(), getAvailableStrategies(), getBestStrategy()

### Community 1 - "Project Architecture & Roadmap"
Cohesion: 0.08
Nodes (43): Platform Abstraction Layer (services/platforms/), summaries DB Table, usage DB Table (free quota tracking), Reusable Code: reel-summary-new.py, Phase 1 — Core Features, Phase 2 — Instagram/YouTube + Payments, Phase 3 — High Concurrency Architecture, Phase 4 — Social / Co-watching Features (+35 more)

### Community 2 - "Platform Downloaders"
Cohesion: 0.12
Nodes (14): ABC, BasePlatform, Abstract base for all platform downloaders. Add a new platform by subclassing an, BasePlatform, InstagramPlatform, Instagram Reels / Posts downloader (yt-dlp + cookie file)., _get_video_duration(), Fetch video duration in seconds without downloading.     Returns None if the pla (+6 more)

### Community 3 - "Bubble Overlay Native Module"
Cohesion: 0.1
Nodes (1): BubbleModule

### Community 4 - "Auth & Usage Tracking"
Cohesion: 0.12
Nodes (12): check_usage(), get_current_user(), increment_usage(), Check usage and raise 403 if the free limit is reached. Returns remaining uses., Increment free_count by 1 after a successful summary., Validate Supabase JWT and return user dict., BaseModel, AppError (+4 more)

### Community 5 - "Accessibility Subtitle Service"
Cohesion: 0.13
Nodes (2): SubtitleAccessibilityService, SubtitleListener

### Community 6 - "Screen Capture Service"
Cohesion: 0.13
Nodes (1): ScreenCaptureService

### Community 7 - "Live Translation Strategy"
Cohesion: 0.14
Nodes (2): AccessibilityServiceStrategy, ScreenCaptureStrategy

### Community 8 - "Screen Capture Native Module"
Cohesion: 0.15
Nodes (1): ScreenCaptureModule

### Community 9 - "i18n Translation Pipeline"
Cohesion: 0.27
Nodes (11): build_ts_file(), full_mode(), main(), parse_existing_i18n(), patch_mode(), Generate UI translations for all languages in languages.ts using Gemini.  Usage:, Ask Gemini to translate all strings to the target language in one call., Parse the auto-generated i18n.ts and return all current translations.     Return (+3 more)

### Community 10 - "Android App Entry"
Cohesion: 0.29
Nodes (1): MainApplication

### Community 11 - "Overlay View"
Cohesion: 0.29
Nodes (2): PositionalOverlayView, TranslatedBlock

### Community 12 - "Android Main Activity"
Cohesion: 0.33
Nodes (1): MainActivity

### Community 13 - "OCR Processor"
Cohesion: 0.33
Nodes (3): OcrProcessor, OcrResult, TextBlock

### Community 14 - "App Branding Assets"
Cohesion: 0.47
Nodes (6): Android Adaptive Icon Foreground, Web Favicon, Android Launcher Icon (xxhdpi), App Icon (icon.png), Splash Screen Icon, Android Splash Screen Logo (xxhdpi)

### Community 15 - "Translation Overlay UI"
Cohesion: 0.4
Nodes (1): TranslationOverlay

### Community 16 - "Bubble Activity"
Cohesion: 0.4
Nodes (1): BubbleActivity

### Community 17 - "Gemini Video Analysis"
Cohesion: 0.5
Nodes (4): analyze_video(), _parse_gemini_response(), Parse Gemini JSON response, falling back gracefully if not valid JSON., Upload video to Gemini, return structured result with summary + AI detection.

### Community 18 - "Bubble Package Registration"
Cohesion: 0.5
Nodes (1): BubblePackage

### Community 19 - "Screen Capture Package Registration"
Cohesion: 0.5
Nodes (1): ScreenCapturePackage

### Community 20 - "Cookie Export Utility"
Cohesion: 1.0
Nodes (2): main(), to_netscape()

### Community 21 - "Railway Env Utility"
Cohesion: 1.0
Nodes (2): main(), upsert_variable()

### Community 22 - "FastAPI Server Entry"
Cohesion: 1.0
Nodes (0): 

### Community 23 - "Job Status API"
Cohesion: 1.0
Nodes (0): 

### Community 24 - "Usage API"
Cohesion: 1.0
Nodes (0): 

### Community 25 - "Backend Tests"
Cohesion: 1.0
Nodes (0): 

### Community 26 - "Platform Download Interface"
Cohesion: 1.0
Nodes (1): Download the video to output_path. Returns True on success.

### Community 27 - "Platform Detection"
Cohesion: 1.0
Nodes (1): Return the platform name for a given URL.

## Knowledge Gaps
- **28 isolated node(s):** `OcrResult`, `TextBlock`, `TranslatedBlock`, `Centralised application error types.  Usage:     raise AppError("USAGE_LIMIT", "`, `Structured application error with a machine-readable code field.` (+23 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `FastAPI Server Entry`** (2 nodes): `main.py`, `health()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Job Status API`** (2 nodes): `jobs.py`, `get_job()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Usage API`** (2 nodes): `usage.py`, `get_usage()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Backend Tests`** (1 nodes): `test.py`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Platform Download Interface`** (1 nodes): `Download the video to output_path. Returns True on success.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Platform Detection`** (1 nodes): `Return the platform name for a given URL.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AccessibilityServiceStrategy` connect `Live Translation Strategy` to `Frontend App Layer`?**
  _High betweenness centrality (0.008) - this node is a cross-community bridge._
- **Why does `ScreenCaptureStrategy` connect `Live Translation Strategy` to `Frontend App Layer`?**
  _High betweenness centrality (0.008) - this node is a cross-community bridge._
- **What connects `OcrResult`, `TextBlock`, `TranslatedBlock` to the rest of the system?**
  _28 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Frontend App Layer` be split into smaller, more focused modules?**
  _Cohesion score 0.07 - nodes in this community are weakly interconnected._
- **Should `Project Architecture & Roadmap` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Platform Downloaders` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._
- **Should `Bubble Overlay Native Module` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._