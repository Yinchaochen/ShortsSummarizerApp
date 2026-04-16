"""
Caption extraction service.

Uses yt-dlp to fetch timed subtitle/caption data from video URLs.
Supports: YouTube (official auto-captions), TikTok (auto-captions where available),
          Instagram (limited), and 1000+ other platforms yt-dlp supports.

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
from typing import Optional

import yt_dlp

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
        ydl_opts = _build_ydl_opts(output_tmpl, url)

        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                info = ydl.extract_info(url, download=True)
        except Exception as e:
            raise RuntimeError(f"yt-dlp extraction failed: {e}") from e

        # 1. Try to read downloaded VTT/SRT file
        segments = _read_subtitle_file(tmpdir)

        # 2. Fall back to parsing from info dict (json3 format)
        if not segments:
            segments = _parse_from_info(info)

        # 3. Try TikTok text stickers from metadata
        stickers = _extract_tiktok_stickers(info)

    if not segments and not stickers:
        raise ValueError("No captions found for this URL. The video may not have auto-captions enabled.")

    all_segments = segments + stickers
    all_segments.sort(key=lambda s: s["start"])
    logger.info("Extracted %d caption segments (%d stickers)", len(segments), len(stickers))
    return all_segments


# ── yt-dlp options ────────────────────────────────────────────────────────────

def _build_ydl_opts(output_tmpl: str, url: str) -> dict:
    opts: dict = {
        "skip_download":   True,
        "writeautosub":    True,
        "writesubtitles":  True,
        "subtitlesformat": "vtt",
        "subtitleslangs":  ["en", "en-US", "en-orig", "en-GB"],
        "outtmpl":         output_tmpl,
        "quiet":           True,
        "no_warnings":     True,
        "http_headers": {
            "User-Agent": (
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/124.0.0.0 Safari/537.36"
            ),
        },
    }

    # Inject cookies if available (needed for some TikTok / YouTube age-restricted)
    if "tiktok.com" in url:
        _inject_cookie(opts, "TIKTOK_COOKIES")
    elif "youtube.com" in url or "youtu.be" in url:
        _inject_cookie(opts, "YOUTUBE_COOKIES")

    return opts


def _inject_cookie(opts: dict, env_var: str) -> None:
    content = os.environ.get(env_var, "").strip()
    if not content:
        return
    tmp = tempfile.NamedTemporaryFile(mode="w", suffix=".txt", delete=False, encoding="utf-8")
    tmp.write(content.replace("\r\n", "\n").replace("\r", "\n"))
    tmp.flush()
    tmp.close()
    opts["cookiefile"] = tmp.name


# ── VTT / subtitle file parsing ───────────────────────────────────────────────

def _read_subtitle_file(tmpdir: str) -> list[dict]:
    for fname in os.listdir(tmpdir):
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
    for source in (info.get("subtitles", {}), info.get("automatic_captions", {})):
        for lang, tracks in source.items():
            if not lang.startswith("en"):
                continue
            for track in tracks:
                if track.get("ext") not in ("json3", "srv3"):
                    continue
                try:
                    segs = _parse_json3(track.get("data") or "")
                    if segs:
                        return segs
                except Exception:
                    pass
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
