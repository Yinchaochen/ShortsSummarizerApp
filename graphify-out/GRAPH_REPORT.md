# Graph Report - .  (2026-04-24)

## Corpus Check
- 83 files · ~288,140 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 488 nodes · 604 edges · 51 communities detected
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 57 edges (avg confidence: 0.5)
- Token cost: 0 input · 0 output

## God Nodes (most connected - your core abstractions)
1. `BasePlatform` - 27 edges
2. `BubbleModule` - 18 edges
3. `ScreenCaptureModule` - 18 edges
4. `PlatformAccessError` - 17 edges
5. `AppError` - 11 edges
6. `PlatformDetectionTests` - 11 edges
7. `main()` - 11 edges
8. `ScreenCaptureService` - 10 edges
9. `ScreenCaptureLiveTranslation` - 10 edges
10. `CloudStreamingTranslator` - 9 edges

## Surprising Connections (you probably didn't know these)
- `Validate Supabase JWT and return user dict.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `Check usage and raise 403 if the free limit is reached. Returns remaining uses.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `Increment free_count by 1 after a successful summary.` --uses--> `AppError`  [INFERRED]
  backend\api\middleware\auth.py → backend\api\errors.py
- `TranscriptRequest` --uses--> `AppError`  [INFERRED]
  backend\api\routes\captions.py → backend\api\errors.py

## Communities

### Community 0 - "Community 0"
Cohesion: 0.07
Nodes (6): ApiError, fetchTranscript(), getAuthHeader(), getUsage(), handleResponse(), submitSummarize()

### Community 1 - "Community 1"
Cohesion: 0.11
Nodes (30): PlatformAccessError, _build_ydl_opts(), _clean_html(), extract_captions(), _extract_tiktok_stickers(), get_captions(), _inject_cookie(), _is_chinese_lang() (+22 more)

### Community 2 - "Community 2"
Cohesion: 0.1
Nodes (14): ABC, BasePlatform, Abstract base for all platform downloaders. Add a new platform by subclassing an, BasePlatform, BilibiliPlatform, Bilibili video downloader (yt-dlp + cookies when required)., InstagramPlatform, Instagram Reels / Posts downloader (yt-dlp + cookie file). (+6 more)

### Community 3 - "Community 3"
Cohesion: 0.09
Nodes (7): BaseModel, TranscriptRequest, SummarizeRequest, CaptionParsingTests, PlatformDetectionTests, RequestValidationTests, TranscriptFormattingTests

### Community 4 - "Community 4"
Cohesion: 0.11
Nodes (1): BubbleModule

### Community 5 - "Community 5"
Cohesion: 0.11
Nodes (1): ScreenCaptureModule

### Community 6 - "Community 6"
Cohesion: 0.28
Nodes (15): build_full_course_dossier(), build_heavy_readme(), build_manifest(), build_playlist_gap_analysis(), build_section_bundle(), clone_official_repo(), extract_pdf_markdown(), is_pdf_url() (+7 more)

### Community 7 - "Community 7"
Cohesion: 0.18
Nodes (12): check_usage(), get_current_user(), _get_or_create_usage(), increment_usage(), Validate Supabase JWT and return user dict., Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed., Check usage and raise 403 if the free limit is reached. Returns remaining uses., Increment free_count by 1 after a successful summary. (+4 more)

### Community 8 - "Community 8"
Cohesion: 0.28
Nodes (14): build_transcript(), _call_gemini_json(), _chunk_segments(), _detect_language(), _format_time_range(), _format_timestamp(), _get_client(), _languages_differ() (+6 more)

### Community 9 - "Community 9"
Cohesion: 0.15
Nodes (1): ScreenCaptureService

### Community 10 - "Community 10"
Cohesion: 0.27
Nodes (11): build_ts_file(), full_mode(), main(), parse_existing_i18n(), patch_mode(), Generate UI translations for all languages in languages.ts using Gemini.  Usage:, Ask Gemini to translate all strings to the target language in one call., Parse the auto-generated i18n.ts and return all current translations.     Return (+3 more)

### Community 11 - "Community 11"
Cohesion: 0.2
Nodes (1): CloudStreamingTranslator

### Community 12 - "Community 12"
Cohesion: 0.2
Nodes (1): IAsrEngine

### Community 13 - "Community 13"
Cohesion: 0.2
Nodes (1): LiveTranslationOrchestrator

### Community 14 - "Community 14"
Cohesion: 0.2
Nodes (1): MediaProjectionCaptureEngine

### Community 15 - "Community 15"
Cohesion: 0.2
Nodes (1): MLKitTranslator

### Community 16 - "Community 16"
Cohesion: 0.2
Nodes (1): PositionalOverlayRenderer

### Community 17 - "Community 17"
Cohesion: 0.2
Nodes (1): SherpaAsrAdapter

### Community 18 - "Community 18"
Cohesion: 0.2
Nodes (1): ScreenCaptureLiveTranslation

### Community 19 - "Community 19"
Cohesion: 0.22
Nodes (2): PositionalOverlayView, TranslatedBlock

### Community 20 - "Community 20"
Cohesion: 0.39
Nodes (8): _build_compressed_path(), _build_ffmpeg_command(), _cleanup(), prepare_video_for_analysis(), Return the path that should be uploaded to Gemini.      If compression is worthw, _safe_getsize(), _should_attempt_compression(), _should_use_compressed_file()

### Community 21 - "Community 21"
Cohesion: 0.25
Nodes (1): MainActivity

### Community 22 - "Community 22"
Cohesion: 0.25
Nodes (2): CaptionSegment, CaptionSyncManager

### Community 23 - "Community 23"
Cohesion: 0.25
Nodes (1): SherpaModelManager

### Community 24 - "Community 24"
Cohesion: 0.25
Nodes (2): Array5, TemporalBackgroundInpainter

### Community 25 - "Community 25"
Cohesion: 0.25
Nodes (1): VideoPreparationTests

### Community 26 - "Community 26"
Cohesion: 0.29
Nodes (1): MainApplication

### Community 27 - "Community 27"
Cohesion: 0.29
Nodes (1): ICaptureEngine

### Community 28 - "Community 28"
Cohesion: 0.29
Nodes (1): ISubtitleRenderer

### Community 29 - "Community 29"
Cohesion: 0.29
Nodes (2): MLKitOcrEngine, Script

### Community 30 - "Community 30"
Cohesion: 0.29
Nodes (1): SherpaAsrManager

### Community 31 - "Community 31"
Cohesion: 0.33
Nodes (5): CaptureConfig, PCMChunk, RenderedSubtitle, SessionConfig, TextBlock

### Community 32 - "Community 32"
Cohesion: 0.47
Nodes (5): _build_probe_opts(), _get_video_duration(), Fetch video duration in seconds without downloading.     Returns None if the pl, Download video, analyze with Gemini, save result to Supabase., summarize_video()

### Community 33 - "Community 33"
Cohesion: 0.4
Nodes (1): BubbleActivity

### Community 34 - "Community 34"
Cohesion: 0.4
Nodes (1): IInpainter

### Community 35 - "Community 35"
Cohesion: 0.4
Nodes (1): ITranslator

### Community 36 - "Community 36"
Cohesion: 0.5
Nodes (4): analyze_video(), _parse_gemini_response(), Parse Gemini JSON response, falling back gracefully if not valid JSON., Upload video to Gemini, return structured result with summary + AI detection.

### Community 37 - "Community 37"
Cohesion: 0.5
Nodes (1): BubblePackage

### Community 38 - "Community 38"
Cohesion: 0.5
Nodes (1): FrameDiffSampler

### Community 39 - "Community 39"
Cohesion: 0.5
Nodes (1): IOcrEngine

### Community 40 - "Community 40"
Cohesion: 0.5
Nodes (1): ScreenCapturePackage

### Community 41 - "Community 41"
Cohesion: 0.67
Nodes (1): IFrameSampler

### Community 42 - "Community 42"
Cohesion: 1.0
Nodes (2): main(), to_netscape()

### Community 43 - "Community 43"
Cohesion: 1.0
Nodes (2): main(), upsert_variable()

### Community 44 - "Community 44"
Cohesion: 1.0
Nodes (0): 

### Community 45 - "Community 45"
Cohesion: 1.0
Nodes (0): 

### Community 46 - "Community 46"
Cohesion: 1.0
Nodes (0): 

### Community 47 - "Community 47"
Cohesion: 1.0
Nodes (0): 

### Community 48 - "Community 48"
Cohesion: 1.0
Nodes (0): 

### Community 49 - "Community 49"
Cohesion: 1.0
Nodes (1): Download the video to output_path. Returns True on success.

### Community 50 - "Community 50"
Cohesion: 1.0
Nodes (1): Return the platform name for a given URL.

## Knowledge Gaps
- **22 isolated node(s):** `CaptionSegment`, `Script`, `CaptureConfig`, `PCMChunk`, `TextBlock` (+17 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 44`** (2 nodes): `main.py`, `health()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 45`** (2 nodes): `jobs.py`, `get_job()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 46`** (2 nodes): `usage.py`, `get_usage()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 47`** (1 nodes): `privacy.tsx`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 48`** (1 nodes): `test.py`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 49`** (1 nodes): `Download the video to output_path. Returns True on success.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 50`** (1 nodes): `Return the platform name for a given URL.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BasePlatform` connect `Community 2` to `Community 32`, `Community 1`, `Community 3`?**
  _High betweenness centrality (0.021) - this node is a cross-community bridge._
- **Why does `TranscriptRequest` connect `Community 3` to `Community 1`, `Community 7`?**
  _High betweenness centrality (0.014) - this node is a cross-community bridge._
- **Why does `AppError` connect `Community 7` to `Community 1`, `Community 3`?**
  _High betweenness centrality (0.013) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `BasePlatform` (e.g. with `Caption extraction service.  Uses yt-dlp to fetch timed subtitle/caption data` and `Extract timed captions from a video URL.      Returns list of:         {"star`) actually correct?**
  _`BasePlatform` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 14 inferred relationships involving `PlatformAccessError` (e.g. with `TranscriptRequest` and `Extract timed captions from a video URL.      Returns a list of caption segmen`) actually correct?**
  _`PlatformAccessError` has 14 INFERRED edges - model-reasoned connections that need verification._
- **Are the 7 inferred relationships involving `AppError` (e.g. with `Validate Supabase JWT and return user dict.` and `Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed.`) actually correct?**
  _`AppError` has 7 INFERRED edges - model-reasoned connections that need verification._
- **What connects `CaptionSegment`, `Script`, `CaptureConfig` to the rest of the system?**
  _22 weakly-connected nodes found - possible documentation gaps or missing edges._