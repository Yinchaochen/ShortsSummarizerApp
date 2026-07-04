# Graph Report - Shorts Summarizer  (2026-07-04)

## Corpus Check
- 85 files · ~289,690 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 536 nodes · 674 edges · 74 communities detected
- Extraction: 76% EXTRACTED · 24% INFERRED · 0% AMBIGUOUS · INFERRED: 160 edges (avg confidence: 0.69)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 60|Community 60]]
- [[_COMMUNITY_Community 61|Community 61]]
- [[_COMMUNITY_Community 63|Community 63]]
- [[_COMMUNITY_Community 64|Community 64]]
- [[_COMMUNITY_Community 65|Community 65]]
- [[_COMMUNITY_Community 66|Community 66]]
- [[_COMMUNITY_Community 67|Community 67]]
- [[_COMMUNITY_Community 68|Community 68]]
- [[_COMMUNITY_Community 69|Community 69]]
- [[_COMMUNITY_Community 70|Community 70]]
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 72|Community 72]]
- [[_COMMUNITY_Community 73|Community 73]]
- [[_COMMUNITY_Community 74|Community 74]]
- [[_COMMUNITY_Community 75|Community 75]]
- [[_COMMUNITY_Community 76|Community 76]]
- [[_COMMUNITY_Community 77|Community 77]]
- [[_COMMUNITY_Community 78|Community 78]]
- [[_COMMUNITY_Community 79|Community 79]]
- [[_COMMUNITY_Community 80|Community 80]]
- [[_COMMUNITY_Community 81|Community 81]]
- [[_COMMUNITY_Community 82|Community 82]]
- [[_COMMUNITY_Community 83|Community 83]]
- [[_COMMUNITY_Community 84|Community 84]]
- [[_COMMUNITY_Community 85|Community 85]]
- [[_COMMUNITY_Community 86|Community 86]]
- [[_COMMUNITY_Community 87|Community 87]]
- [[_COMMUNITY_Community 88|Community 88]]
- [[_COMMUNITY_Community 89|Community 89]]
- [[_COMMUNITY_Community 90|Community 90]]
- [[_COMMUNITY_Community 91|Community 91]]
- [[_COMMUNITY_Community 92|Community 92]]
- [[_COMMUNITY_Community 93|Community 93]]
- [[_COMMUNITY_Community 94|Community 94]]
- [[_COMMUNITY_Community 95|Community 95]]

## God Nodes (most connected - your core abstractions)
1. `BasePlatform` - 29 edges
2. `PlatformAccessError` - 20 edges
3. `BubbleModule` - 18 edges
4. `ScreenCaptureModule` - 18 edges
5. `AppError` - 15 edges
6. `extract_captions()` - 13 edges
7. `detect()` - 12 edges
8. `PlatformDetectionTests` - 11 edges
9. `main()` - 11 edges
10. `ScreenCaptureService` - 10 edges

## Surprising Connections (you probably didn't know these)
- `BasePlatform` --uses--> `Download a video from any supported platform and ask Gemini to describe UI in ex`  [INFERRED]
  backend\services\platforms\base.py → scripts\describe_ui.py
- `BasePlatform` --uses--> `One-off summarize: download a Bilibili video and analyze with Gemini.  Bypasses`  [INFERRED]
  backend\services\platforms\base.py → scripts\summarize_one.py
- `AppError` --uses--> `Validate Supabase JWT and return user dict.`  [INFERRED]
  backend\api\errors.py → backend\api\middleware\auth.py
- `AppError` --uses--> `Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed.`  [INFERRED]
  backend\api\errors.py → backend\api\middleware\auth.py
- `AppError` --uses--> `Check usage and raise 403 if the free limit is reached. Returns remaining uses.`  [INFERRED]
  backend\api\errors.py → backend\api\middleware\auth.py

## Communities

### Community 0 - "Community 0"
Cohesion: 0.07
Nodes (31): ABC, BasePlatform, SherpaAsrManager, add_cookie_file(), BasePlatform, cleanup_cookie_file(), detect(), is_bilibili_access_blocked() (+23 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (4): checkSharedUrl(), handleDownloadAsrModels(), ScreenCaptureModule, ScreenCaptureLiveTranslation

### Community 2 - "Community 2"
Cohesion: 0.11
Nodes (20): AppError, Centralised application error types.  Usage:     raise AppError("USAGE_LIMIT", ", Structured application error with a machine-readable code field., HTTPException, check_usage(), get_current_user(), _get_or_create_usage(), increment_usage() (+12 more)

### Community 3 - "Community 3"
Cohesion: 0.17
Nodes (20): build_full_course_dossier(), build_heavy_readme(), build_manifest(), build_playlist_gap_analysis(), build_section_bundle(), clone_official_repo(), extract_pdf_markdown(), is_pdf_url() (+12 more)

### Community 4 - "Community 4"
Cohesion: 0.16
Nodes (20): _clean_html(), _extract_tiktok_stickers(), _is_chinese_lang(), _is_english_lang(), _merge_duplicate_segments(), _parse_json3(), _parse_srt(), _parse_track_data() (+12 more)

### Community 5 - "Community 5"
Cohesion: 0.18
Nodes (18): RuntimeError, extract_captions(), Extract timed captions from a video URL.      Returns list of:         {"star, build_transcript(), _call_gemini_json(), _chunk_segments(), _detect_language(), _format_time_range() (+10 more)

### Community 6 - "Community 6"
Cohesion: 0.14
Nodes (13): analyze_video(), _parse_gemini_response(), Parse Gemini JSON response, falling back gracefully if not valid JSON., Upload video to Gemini, return structured result with summary + AI detection., _build_compressed_path(), _build_ffmpeg_command(), _cleanup(), prepare_video_for_analysis() (+5 more)

### Community 7 - "Community 7"
Cohesion: 0.11
Nodes (1): BubbleModule

### Community 8 - "Community 8"
Cohesion: 0.22
Nodes (7): BaseModel, TranscriptRequest, SummarizeRequest, _parse_from_info(), Parse captions directly from yt-dlp info dict when no file was written., CaptionParsingTests, RequestValidationTests

### Community 9 - "Community 9"
Cohesion: 0.15
Nodes (1): ScreenCaptureService

### Community 10 - "Community 10"
Cohesion: 0.23
Nodes (7): handleSubmit(), ApiError, fetchTranscript(), getAuthHeader(), getUsage(), handleResponse(), submitSummarize()

### Community 11 - "Community 11"
Cohesion: 0.27
Nodes (11): build_ts_file(), full_mode(), main(), parse_existing_i18n(), patch_mode(), Generate UI translations for all languages in languages.ts using Gemini.  Usage:, Ask Gemini to translate all strings to the target language in one call., Parse the auto-generated i18n.ts and return all current translations.     Return (+3 more)

### Community 12 - "Community 12"
Cohesion: 0.2
Nodes (1): CloudStreamingTranslator

### Community 13 - "Community 13"
Cohesion: 0.2
Nodes (1): IAsrEngine

### Community 14 - "Community 14"
Cohesion: 0.2
Nodes (1): LiveTranslationOrchestrator

### Community 15 - "Community 15"
Cohesion: 0.2
Nodes (1): MediaProjectionCaptureEngine

### Community 16 - "Community 16"
Cohesion: 0.2
Nodes (1): MLKitTranslator

### Community 17 - "Community 17"
Cohesion: 0.2
Nodes (1): PositionalOverlayRenderer

### Community 18 - "Community 18"
Cohesion: 0.2
Nodes (1): SherpaAsrAdapter

### Community 19 - "Community 19"
Cohesion: 0.25
Nodes (1): MainActivity

### Community 20 - "Community 20"
Cohesion: 0.25
Nodes (2): CaptionSegment, CaptionSyncManager

### Community 21 - "Community 21"
Cohesion: 0.25
Nodes (2): PositionalOverlayView, TranslatedBlock

### Community 22 - "Community 22"
Cohesion: 0.25
Nodes (1): SherpaModelManager

### Community 23 - "Community 23"
Cohesion: 0.25
Nodes (2): Array5, TemporalBackgroundInpainter

### Community 24 - "Community 24"
Cohesion: 0.29
Nodes (1): MainApplication

### Community 25 - "Community 25"
Cohesion: 0.29
Nodes (1): ICaptureEngine

### Community 26 - "Community 26"
Cohesion: 0.29
Nodes (1): ISubtitleRenderer

### Community 27 - "Community 27"
Cohesion: 0.29
Nodes (2): MLKitOcrEngine, Script

### Community 28 - "Community 28"
Cohesion: 0.29
Nodes (3): LoginScreen(), AIDetectionCard(), useLanguage()

### Community 29 - "Community 29"
Cohesion: 0.33
Nodes (5): CaptureConfig, PCMChunk, RenderedSubtitle, SessionConfig, TextBlock

### Community 30 - "Community 30"
Cohesion: 0.4
Nodes (1): BubbleActivity

### Community 31 - "Community 31"
Cohesion: 0.4
Nodes (1): IInpainter

### Community 32 - "Community 32"
Cohesion: 0.4
Nodes (1): ITranslator

### Community 33 - "Community 33"
Cohesion: 0.5
Nodes (1): BubblePackage

### Community 34 - "Community 34"
Cohesion: 0.5
Nodes (1): FrameDiffSampler

### Community 35 - "Community 35"
Cohesion: 0.5
Nodes (1): IOcrEngine

### Community 36 - "Community 36"
Cohesion: 0.5
Nodes (1): ScreenCapturePackage

### Community 37 - "Community 37"
Cohesion: 0.67
Nodes (1): IFrameSampler

### Community 39 - "Community 39"
Cohesion: 1.0
Nodes (2): main(), upsert_variable()

### Community 60 - "Community 60"
Cohesion: 1.0
Nodes (1): Download the video to output_path. Returns True on success.

### Community 61 - "Community 61"
Cohesion: 1.0
Nodes (1): Return the platform name for a given URL.

### Community 63 - "Community 63"
Cohesion: 1.0
Nodes (1): Centralised application error types.  Usage:     raise AppError("USAGE_LIMIT", "

### Community 64 - "Community 64"
Cohesion: 1.0
Nodes (1): Structured application error with a machine-readable code field.

### Community 65 - "Community 65"
Cohesion: 1.0
Nodes (1): Validate Supabase JWT and return user dict.

### Community 66 - "Community 66"
Cohesion: 1.0
Nodes (1): Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed.

### Community 67 - "Community 67"
Cohesion: 1.0
Nodes (1): Check usage and raise 403 if the free limit is reached. Returns remaining uses.

### Community 68 - "Community 68"
Cohesion: 1.0
Nodes (1): Increment free_count by 1 after a successful summary.

### Community 69 - "Community 69"
Cohesion: 1.0
Nodes (1): Extract timed captions from a video URL.      Returns a list of caption segmen

### Community 70 - "Community 70"
Cohesion: 1.0
Nodes (1): Caption extraction service.  Uses yt-dlp to fetch timed subtitle/caption data

### Community 71 - "Community 71"
Cohesion: 1.0
Nodes (1): Extract timed captions from a video URL.      Returns list of:         {"star

### Community 72 - "Community 72"
Cohesion: 1.0
Nodes (1): Parse WebVTT into timed segments.

### Community 73 - "Community 73"
Cohesion: 1.0
Nodes (1): Parse SRT into timed segments.

### Community 74 - "Community 74"
Cohesion: 1.0
Nodes (1): Parse captions directly from yt-dlp info dict when no file was written.

### Community 75 - "Community 75"
Cohesion: 1.0
Nodes (1): Parse YouTube json3 caption format.

### Community 76 - "Community 76"
Cohesion: 1.0
Nodes (1): Extract creator-added text stickers from TikTok video metadata.      TikTok st

### Community 77 - "Community 77"
Cohesion: 1.0
Nodes (1): Remove consecutive duplicate lines (common in auto-captions).

### Community 78 - "Community 78"
Cohesion: 1.0
Nodes (1): Parse Gemini JSON response, falling back gracefully if not valid JSON.

### Community 79 - "Community 79"
Cohesion: 1.0
Nodes (1): Upload video to Gemini, return structured result with summary + AI detection.

### Community 80 - "Community 80"
Cohesion: 1.0
Nodes (1): Return the path that should be uploaded to Gemini.      If compression is worthw

### Community 81 - "Community 81"
Cohesion: 1.0
Nodes (1): Abstract base for all platform downloaders. Add a new platform by subclassing an

### Community 82 - "Community 82"
Cohesion: 1.0
Nodes (1): Download the video to output_path. Returns True on success.

### Community 83 - "Community 83"
Cohesion: 1.0
Nodes (1): Return the platform name for a given URL.

### Community 84 - "Community 84"
Cohesion: 1.0
Nodes (1): Bilibili video downloader (yt-dlp + cookies when required).

### Community 85 - "Community 85"
Cohesion: 1.0
Nodes (1): Instagram Reels / Posts downloader (yt-dlp + cookie file).

### Community 86 - "Community 86"
Cohesion: 1.0
Nodes (1): TikTok video downloader (yt-dlp).

### Community 87 - "Community 87"
Cohesion: 1.0
Nodes (1): Xiaohongshu / Rednote video downloader (yt-dlp).

### Community 88 - "Community 88"
Cohesion: 1.0
Nodes (1): YouTube / YouTube Shorts video downloader (yt-dlp).

### Community 89 - "Community 89"
Cohesion: 1.0
Nodes (1): Fetch video duration in seconds without downloading.     Returns None if the pl

### Community 90 - "Community 90"
Cohesion: 1.0
Nodes (1): Download video, analyze with Gemini, save result to Supabase.

### Community 91 - "Community 91"
Cohesion: 1.0
Nodes (1): Generate UI translations for all languages in languages.ts using Gemini.  Usage:

### Community 92 - "Community 92"
Cohesion: 1.0
Nodes (1): Ask Gemini to translate all strings to the target language in one call.

### Community 93 - "Community 93"
Cohesion: 1.0
Nodes (1): Parse the auto-generated i18n.ts and return all current translations.     Return

### Community 94 - "Community 94"
Cohesion: 1.0
Nodes (1): Translate all keys for all languages from scratch.

### Community 95 - "Community 95"
Cohesion: 1.0
Nodes (1): Translate only the specified keys for all languages, merging into the     existi

## Knowledge Gaps
- **55 isolated node(s):** `CaptionSegment`, `Script`, `CaptureConfig`, `PCMChunk`, `TextBlock` (+50 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 7`** (19 nodes): `BubbleModule.kt`, `BubbleModule`, `.addListener()`, `.checkPermissions()`, `.dismissBubble()`, `.ensureChannel()`, `.getName()`, `.isSupported()`, `.makeIcon()`, `.ping()`, `.removeListeners()`, `.requestAccessibilityPermission()`, `.setOverlayEnabled()`, `.setTargetLanguage()`, `.showPetBubble()`, `.showTranslationBubble()`, `.start()`, `.stop()`, `.updateConfig()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 9`** (13 nodes): `ScreenCaptureService.kt`, `langToOcrScript()`, `ScreenCaptureService`, `.buildNotification()`, `.buildOrchestrator()`, `.createNotificationChannel()`, `.handleStart()`, `.onBind()`, `.onCreate()`, `.onDestroy()`, `.onStartCommand()`, `.stopSession()`, `setAudioEnabled()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (10 nodes): `CloudStreamingTranslator.kt`, `CloudStreamingTranslator`, `.buildPrompt()`, `.buildRequest()`, `.cancel()`, `.langName()`, `.parseToken()`, `.release()`, `.streamTranslation()`, `.translate()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 13`** (10 nodes): `IAsrEngine.kt`, `IAsrEngine`, `.feedAudio()`, `.flush()`, `.isReady()`, `.release()`, `.setFinalResultCallback()`, `.setPartialResultCallback()`, `.start()`, `.stop()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 14`** (10 nodes): `LiveTranslationOrchestrator.kt`, `LiveTranslationOrchestrator`, `.applyBatchResult()`, `.release()`, `.setupAsrCallbacks()`, `.start()`, `.stop()`, `.translateAsync()`, `.translateBlocks()`, `.videoLoop()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (10 nodes): `MediaProjectionCaptureEngine.kt`, `MediaProjectionCaptureEngine`, `.acquireFrame()`, `.frameLoop()`, `.release()`, `.setAudioCallback()`, `.setFrameCallback()`, `.start()`, `.startAudioCapture()`, `.stop()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (10 nodes): `MLKitTranslator.kt`, `MLKitTranslator`, `.cancel()`, `.ensureModelReady()`, `.getOrCreateClient()`, `.release()`, `.resolveSourceLang()`, `.toMlKitCode()`, `.translate()`, `.translateSuspend()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 17`** (10 nodes): `PositionalOverlayRenderer.kt`, `PositionalOverlayRenderer`, `.buildAudioBar()`, `.buildBubble()`, `.buildCoverPatch()`, `.hide()`, `.release()`, `.show()`, `.update()`, `.updateAudioBar()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 18`** (10 nodes): `SherpaAsrAdapter.kt`, `SherpaAsrAdapter`, `.feedAudio()`, `.flush()`, `.isReady()`, `.release()`, `.setFinalResultCallback()`, `.setPartialResultCallback()`, `.start()`, `.stop()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 19`** (8 nodes): `MainActivity.kt`, `MainActivity`, `.createReactActivityDelegate()`, `.getMainComponentName()`, `.handleShareIntent()`, `.invokeDefaultOnBackPressed()`, `.onCreate()`, `.onNewIntent()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 20`** (8 nodes): `CaptionSyncManager.kt`, `CaptionSegment`, `CaptionSyncManager`, `.load()`, `.start()`, `.stop()`, `.toRenderedSubtitle()`, `run()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 21`** (8 nodes): `PositionalOverlayView.kt`, `PositionalOverlayView`, `.buildBubble()`, `.buildSubtitleBar()`, `.hide()`, `.show()`, `.updateSubtitleBar()`, `TranslatedBlock`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 22`** (8 nodes): `SherpaModelManager.kt`, `SherpaModelManager`, `.areModelsReady()`, `.cancel()`, `.downloadFile()`, `.downloadModels()`, `.estimatedMb()`, `.isNonEmpty()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 23`** (8 nodes): `TemporalBackgroundInpainter.kt`, `Array5`, `TemporalBackgroundInpainter`, `.inpaint()`, `.isInsideAny()`, `.release()`, `.sampleColor()`, `.updateBackground()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 24`** (7 nodes): `MainApplication.kt`, `getJSMainModuleName()`, `getPackages()`, `getUseDeveloperSupport()`, `MainApplication`, `.onConfigurationChanged()`, `.onCreate()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 25`** (7 nodes): `ICaptureEngine.kt`, `ICaptureEngine`, `.release()`, `.setAudioCallback()`, `.setFrameCallback()`, `.start()`, `.stop()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 26`** (7 nodes): `ISubtitleRenderer.kt`, `ISubtitleRenderer`, `.hide()`, `.release()`, `.show()`, `.update()`, `.updateAudioBar()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 27`** (7 nodes): `MLKitOcrEngine.kt`, `await()`, `MLKitOcrEngine`, `.detect()`, `.filterChrome()`, `.release()`, `Script`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 30`** (5 nodes): `BubbleActivity.kt`, `BubbleActivity`, `.buildPetView()`, `.buildTranslationView()`, `.onCreate()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 31`** (5 nodes): `IInpainter.kt`, `IInpainter`, `.inpaint()`, `.release()`, `.updateBackground()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 32`** (5 nodes): `ITranslator.kt`, `ITranslator`, `.cancel()`, `.release()`, `.translate()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 33`** (4 nodes): `BubblePackage.kt`, `BubblePackage`, `.createNativeModules()`, `.createViewManagers()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 34`** (4 nodes): `FrameDiffSampler.kt`, `FrameDiffSampler`, `.luminanceDiff()`, `.shouldProcess()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 35`** (4 nodes): `IOcrEngine.kt`, `IOcrEngine`, `.detect()`, `.release()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 36`** (4 nodes): `ScreenCapturePackage.kt`, `ScreenCapturePackage`, `.createNativeModules()`, `.createViewManagers()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 37`** (3 nodes): `IFrameSampler.kt`, `IFrameSampler`, `.shouldProcess()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 39`** (3 nodes): `main()`, `update_railway_env.py`, `upsert_variable()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 60`** (1 nodes): `Download the video to output_path. Returns True on success.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 61`** (1 nodes): `Return the platform name for a given URL.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 63`** (1 nodes): `Centralised application error types.  Usage:     raise AppError("USAGE_LIMIT", "`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 64`** (1 nodes): `Structured application error with a machine-readable code field.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 65`** (1 nodes): `Validate Supabase JWT and return user dict.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 66`** (1 nodes): `Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 67`** (1 nodes): `Check usage and raise 403 if the free limit is reached. Returns remaining uses.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 68`** (1 nodes): `Increment free_count by 1 after a successful summary.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 69`** (1 nodes): `Extract timed captions from a video URL.      Returns a list of caption segmen`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 70`** (1 nodes): `Caption extraction service.  Uses yt-dlp to fetch timed subtitle/caption data`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 71`** (1 nodes): `Extract timed captions from a video URL.      Returns list of:         {"star`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 72`** (1 nodes): `Parse WebVTT into timed segments.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 73`** (1 nodes): `Parse SRT into timed segments.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 74`** (1 nodes): `Parse captions directly from yt-dlp info dict when no file was written.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 75`** (1 nodes): `Parse YouTube json3 caption format.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 76`** (1 nodes): `Extract creator-added text stickers from TikTok video metadata.      TikTok st`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 77`** (1 nodes): `Remove consecutive duplicate lines (common in auto-captions).`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 78`** (1 nodes): `Parse Gemini JSON response, falling back gracefully if not valid JSON.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 79`** (1 nodes): `Upload video to Gemini, return structured result with summary + AI detection.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 80`** (1 nodes): `Return the path that should be uploaded to Gemini.      If compression is worthw`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 81`** (1 nodes): `Abstract base for all platform downloaders. Add a new platform by subclassing an`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 82`** (1 nodes): `Download the video to output_path. Returns True on success.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 83`** (1 nodes): `Return the platform name for a given URL.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 84`** (1 nodes): `Bilibili video downloader (yt-dlp + cookies when required).`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 85`** (1 nodes): `Instagram Reels / Posts downloader (yt-dlp + cookie file).`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 86`** (1 nodes): `TikTok video downloader (yt-dlp).`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 87`** (1 nodes): `Xiaohongshu / Rednote video downloader (yt-dlp).`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 88`** (1 nodes): `YouTube / YouTube Shorts video downloader (yt-dlp).`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 89`** (1 nodes): `Fetch video duration in seconds without downloading.     Returns None if the pl`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 90`** (1 nodes): `Download video, analyze with Gemini, save result to Supabase.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 91`** (1 nodes): `Generate UI translations for all languages in languages.ts using Gemini.  Usage:`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 92`** (1 nodes): `Ask Gemini to translate all strings to the target language in one call.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 93`** (1 nodes): `Parse the auto-generated i18n.ts and return all current translations.     Return`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 94`** (1 nodes): `Translate all keys for all languages from scratch.`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 95`** (1 nodes): `Translate only the specified keys for all languages, merging into the     existi`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `write_text()` connect `Community 3` to `Community 11`, `Community 4`?**
  _High betweenness centrality (0.033) - this node is a cross-community bridge._
- **Why does `summarize_video()` connect `Community 0` to `Community 2`, `Community 3`, `Community 6`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **Why does `analyze_video()` connect `Community 6` to `Community 0`, `Community 3`, `Community 5`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **Are the 26 inferred relationships involving `BasePlatform` (e.g. with `Caption extraction service.  Uses yt-dlp to fetch timed subtitle/caption data` and `Extract timed captions from a video URL.      Returns list of:         {"star`) actually correct?**
  _`BasePlatform` has 26 INFERRED edges - model-reasoned connections that need verification._
- **Are the 17 inferred relationships involving `PlatformAccessError` (e.g. with `TranscriptRequest` and `Extract timed captions from a video URL.      Returns a list of caption segmen`) actually correct?**
  _`PlatformAccessError` has 17 INFERRED edges - model-reasoned connections that need verification._
- **Are the 11 inferred relationships involving `AppError` (e.g. with `Validate Supabase JWT and return user dict.` and `Fetch usage row, creating it if absent. Resets free_count if 24h has elapsed.`) actually correct?**
  _`AppError` has 11 INFERRED edges - model-reasoned connections that need verification._
- **What connects `CaptionSegment`, `Script`, `CaptureConfig` to the rest of the system?**
  _55 weakly-connected nodes found - possible documentation gaps or missing edges._