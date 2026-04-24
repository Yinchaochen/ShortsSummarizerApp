"""
Caption extraction service.

Uses yt-dlp to fetch timed subtitle/caption data from video URLs.
Supports: YouTube (official auto-captions), TikTok (auto-captions where available),
          Instagram (limited), Bilibili, and 1000+ other platforms yt-dlp supports.

Returns a flat list of CaptionSegment dicts:
  [{"start": 1.23, "end": 4.56, "text": "Hello world", "x": None, "y": None}]

"x" and "y" are relative screen coordinates (0.0–1.0) for text stickers.
Speech captions have x=None, y=None (display as standard subtitle bar).
"""

from __future__ import annotations

import os
import re
import tempfile
import json
import logging

import yt_dlp
from services.platforms.base import (
    BILIBILI_ACCESS_BLOCKED_CODE,
    BILIBILI_ACCESS_BLOCKED_MESSAGE,
    BasePlatform,
    PlatformAccessError,
)

logger = logging.getLogger(__name__)


# ── Public API ────────────────────────────────────────────────────────────────

def extract_captions(url: str) -> list[dict]:
    """
    Extract timed captions from a video URL.

    Returns list of:
        {"start": float, "end": float, "text": str, "x": float|None, "y": float|None}

    Raises ValueError if no captions are found.
    Raises RuntimeError on yt-dlp extraction failure.
    """
    logger.info("Extracting captions from: %s", url)

    with tempfile.TemporaryDirectory() as tmpdir:
        output_tmpl = os.path.join(tmpdir, "cap")
        ydl_opts, cookie_file = _build_ydl_opts(output_tmpl, url)

        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                info = ydl.extract_info(url, download=True)
        except Exception as e:
            if BasePlatform.detect(url) == "bilibili" and BasePlatform.is_bilibili_access_blocked(e):
                raise PlatformAccessError(
                    BILIBILI_ACCESS_BLOCKED_CODE,
                    BILIBILI_ACCESS_BLOCKED_MESSAGE,
                ) from e
            raise RuntimeError(f"yt-dlp extraction failed: {e}") from e
        finally:
            BasePlatform.cleanup_cookie_file(cookie_file)

        # 1. Prefer ordered parsing from the info dict.
        segments = _parse_from_info(info)

        # 2. Fall back to downloaded subtitle files if the extractor did not inline data.
        if not segments:
            segments = _read_subtitle_file(tmpdir)

        # 3. Try TikTok text stickers from metadata
        stickers = _extract_tiktok_stickers(info)

    if not segments and not stickers:
        raise ValueError("No captions found for this URL. The video may not have auto-captions enabled.")

    all_segments = segments + stickers
    all_segments.sort(key=lambda s: s["start"])
    logger.info("Extracted %d caption segments (%d stickers)", len(segments), len(stickers))
    return all_segments


# ── yt-dlp options ────────────────────────────────────────────────────────────

def _build_ydl_opts(output_tmpl: str, url: str) -> tuple[dict, str | None]:
    opts: dict = {
        "skip_download":   True,
        "writeautosub":    True,
        "writesubtitles":  True,
        "subtitlesformat": "vtt",
        "subtitleslangs":  ["all"],
        "outtmpl":         output_tmpl,
        "quiet":           True,
        "no_warnings":     True,
        "http_headers":    dict(BasePlatform.BROWSER_HEADERS),
    }

    cookie_file = None
    platform = BasePlatform.detect(url)

    # Inject cookies if available (needed for some TikTok / YouTube age-restricted)
    if platform == "tiktok":
        cookie_file = _inject_cookie(opts, "TIKTOK_COOKIES")
    elif platform == "youtube":
        cookie_file = _inject_cookie(opts, "YOUTUBE_COOKIES")
    elif platform == "bilibili":
        opts["http_headers"] = dict(BasePlatform.BILIBILI_HEADERS)
        cookie_file = _inject_cookie(opts, "BILIBILI_COOKIES")

    return opts, cookie_file


def _inject_cookie(opts: dict, env_var: str) -> str | None:
    return BasePlatform.add_cookie_file(opts, env_var)


# ── VTT / subtitle file parsing ───────────────────────────────────────────────

def _read_subtitle_file(tmpdir: str) -> list[dict]:
    subtitle_files = sorted(
        (fname for fname in os.listdir(tmpdir) if fname.endswith((".vtt", ".srt"))),
        key=_subtitle_file_sort_key,
    )

    for fname in subtitle_files:
        if fname.endswith(".vtt"):
            with open(os.path.join(tmpdir, fname), encoding="utf-8") as f:
                return _parse_vtt(f.read())
        if fname.endswith(".srt"):
            with open(os.path.join(tmpdir, fname), encoding="utf-8") as f:
                return _parse_srt(f.read())
    return []


def _parse_vtt(content: str) -> list[dict]:
    """Parse WebVTT into timed segments."""
    segments: list[dict] = []
    lines = content.splitlines()
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        if "-->" in line:
            time_part = line.split(" --> ")
            start = _vtt_time(time_part[0].strip())
            end   = _vtt_time(time_part[1].split()[0].strip())
            i += 1
            text_parts: list[str] = []
            while i < len(lines) and lines[i].strip():
                text_parts.append(lines[i].strip())
                i += 1
            text = _clean_html(" ".join(text_parts))
            if text:
                segments.append({"start": start, "end": end, "text": text, "x": None, "y": None})
        i += 1
    return _merge_duplicate_segments(segments)


def _parse_srt(content: str) -> list[dict]:
    """Parse SRT into timed segments."""
    segments: list[dict] = []
    blocks = re.split(r"\n\n+", content.strip())
    for block in blocks:
        lines = block.strip().splitlines()
        if len(lines) < 3:
            continue
        if "-->" not in lines[1]:
            continue
        time_parts = lines[1].split(" --> ")
        start = _srt_time(time_parts[0].strip())
        end   = _srt_time(time_parts[1].strip())
        text  = _clean_html(" ".join(lines[2:]))
        if text:
            segments.append({"start": start, "end": end, "text": text, "x": None, "y": None})
    return segments


# ── Fallback: parse from yt-dlp info dict (json3 / srv3) ─────────────────────

def _parse_from_info(info: dict) -> list[dict]:
    """Parse captions directly from yt-dlp info dict when no file was written."""
    subtitles = info.get("subtitles", {}) or {}
    automatic = info.get("automatic_captions", {}) or {}

    for source, matcher in (
        (subtitles, _is_english_lang),
        (automatic, _is_english_lang),
        (subtitles, _is_chinese_lang),
        (automatic, _is_chinese_lang),
        (subtitles, lambda _: True),
        (automatic, lambda _: True),
    ):
        for lang, tracks in source.items():
            if not matcher(lang):
                continue
            for track in tracks:
                segs = _parse_track_data(track)
                if segs:
                    return segs
    return []


def _parse_json3(data: str) -> list[dict]:
    """Parse YouTube json3 caption format."""
    if not data:
        return []
    obj = json.loads(data)
    segments: list[dict] = []
    for event in obj.get("events", []):
        start_ms = event.get("tStartMs", 0)
        dur_ms   = event.get("dDurationMs", 0)
        segs     = event.get("segs", [])
        text     = _clean_html("".join(s.get("utf8", "") for s in segs)).strip()
        if text and dur_ms > 0:
            segments.append({
                "start": start_ms / 1000,
                "end":   (start_ms + dur_ms) / 1000,
                "text":  text,
                "x": None, "y": None,
            })
    return segments


def _parse_track_data(track: dict) -> list[dict]:
    ext = (track.get("ext") or "").lower()
    data = track.get("data") or ""

    try:
        if ext in ("json3", "srv3"):
            return _parse_json3(data)
        if ext == "vtt":
            return _parse_vtt(data)
        if ext == "srt":
            return _parse_srt(data)
    except Exception:
        return []

    return []


def _is_english_lang(lang: str) -> bool:
    return lang.lower().startswith("en")


def _is_chinese_lang(lang: str) -> bool:
    lang = lang.lower()
    return lang.startswith("zh") or "chinese" in lang


def _subtitle_file_sort_key(fname: str) -> tuple[int, str]:
    lang = _subtitle_lang_from_filename(fname)
    if _is_english_lang(lang):
        return (0, fname)
    if _is_chinese_lang(lang):
        return (1, fname)
    return (2, fname)


def _subtitle_lang_from_filename(fname: str) -> str:
    parts = fname.split(".")
    if len(parts) < 3:
        return ""
    return parts[-2]


# ── TikTok text sticker extraction ────────────────────────────────────────────

def _extract_tiktok_stickers(info: dict) -> list[dict]:
    """
    Extract creator-added text stickers from TikTok video metadata.

    TikTok stores these in info['textExtra'] or info['stickersOnItem'].
    Each sticker has text content and relative screen position (x, y: 0.0–1.0).
    Timing is approximate (stickers often show for the full video duration).
    """
    stickers: list[dict] = []
    duration = float(info.get("duration") or 0)

    # textExtra: [{text, x, y, ...}, ...]
    for item in info.get("textExtra", []) or []:
        text = _clean_html(item.get("text") or "").strip()
        if not text or len(text) < 2:
            continue
        stickers.append({
            "start": 0.0,
            "end":   duration or 30.0,
            "text":  text,
            "x":     item.get("x"),
            "y":     item.get("y"),
        })

    # stickersOnItem (alternative field name)
    for item in info.get("stickersOnItem", []) or []:
        text = _clean_html(item.get("stickerText") or "").strip()
        if not text or len(text) < 2:
            continue
        stickers.append({
            "start": float(item.get("startTime") or 0),
            "end":   float(item.get("endTime") or duration or 30.0),
            "text":  text,
            "x":     item.get("x"),
            "y":     item.get("y"),
        })

    return stickers


# ── Utilities ─────────────────────────────────────────────────────────────────

def _vtt_time(t: str) -> float:
    parts = t.replace(",", ".").split(":")
    if len(parts) == 3:
        return int(parts[0]) * 3600 + int(parts[1]) * 60 + float(parts[2])
    return int(parts[0]) * 60 + float(parts[1])


def _srt_time(t: str) -> float:
    return _vtt_time(t.replace(",", "."))


def _clean_html(text: str) -> str:
    text = re.sub(r"<[^>]+>", "", text)
    text = text.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ")
    return re.sub(r"\s+", " ", text).strip()


def _merge_duplicate_segments(segments: list[dict]) -> list[dict]:
    """Remove consecutive duplicate lines (common in auto-captions)."""
    if not segments:
        return []
    merged = [segments[0]]
    for seg in segments[1:]:
        if seg["text"] == merged[-1]["text"]:
            merged[-1]["end"] = seg["end"]   # extend duration
        else:
            merged.append(seg)
    return merged
