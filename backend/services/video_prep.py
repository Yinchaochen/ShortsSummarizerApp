from __future__ import annotations

import logging
import os
import subprocess
from pathlib import Path


logger = logging.getLogger(__name__)

UPLOAD_SIZE_TARGET_MB = 20
MIN_SHRINK_RATIO = 0.90
FFMPEG_TIMEOUT_SECONDS = 180


def prepare_video_for_analysis(video_path: str) -> tuple[str, str | None]:
    """
    Return the path that should be uploaded to Gemini.

    If compression is worthwhile, returns (compressed_path, compressed_path).
    Otherwise returns (original_path, None).
    """
    original_size = _safe_getsize(video_path)
    if original_size <= 0:
        return video_path, None

    if not _should_attempt_compression(original_size):
        return video_path, None

    compressed_path = _build_compressed_path(video_path)
    command = _build_ffmpeg_command(video_path, compressed_path)

    try:
        subprocess.run(
            command,
            check=True,
            capture_output=True,
            text=True,
            timeout=FFMPEG_TIMEOUT_SECONDS,
        )
    except FileNotFoundError:
        logger.warning("ffmpeg not found; skipping video compression")
        _cleanup(compressed_path)
        return video_path, None
    except subprocess.TimeoutExpired:
        logger.warning("ffmpeg compression timed out for %s", video_path)
        _cleanup(compressed_path)
        return video_path, None
    except subprocess.CalledProcessError as exc:
        stderr = (exc.stderr or "").strip()
        logger.warning("ffmpeg compression failed: %s", stderr[-400:])
        _cleanup(compressed_path)
        return video_path, None

    compressed_size = _safe_getsize(compressed_path)
    if not _should_use_compressed_file(original_size, compressed_size):
        _cleanup(compressed_path)
        return video_path, None

    logger.info(
        "Prepared compressed upload: %.2f MB -> %.2f MB",
        original_size / (1024 * 1024),
        compressed_size / (1024 * 1024),
    )
    return compressed_path, compressed_path


def _safe_getsize(path: str) -> int:
    try:
        return os.path.getsize(path)
    except OSError:
        return -1


def _should_attempt_compression(original_size_bytes: int) -> bool:
    return original_size_bytes > UPLOAD_SIZE_TARGET_MB * 1024 * 1024


def _should_use_compressed_file(original_size_bytes: int, compressed_size_bytes: int) -> bool:
    if compressed_size_bytes <= 0 or compressed_size_bytes >= original_size_bytes:
        return False

    target_bytes = UPLOAD_SIZE_TARGET_MB * 1024 * 1024
    if compressed_size_bytes <= target_bytes:
        return True

    return compressed_size_bytes <= int(original_size_bytes * MIN_SHRINK_RATIO)


def _build_compressed_path(video_path: str) -> str:
    path = Path(video_path)
    return str(path.with_name(f"{path.stem}_gemini.mp4"))


def _build_ffmpeg_command(input_path: str, output_path: str) -> list[str]:
    return [
        "ffmpeg",
        "-y",
        "-i",
        input_path,
        "-vf",
        (
            "scale="
            "'if(gte(iw,ih),trunc(min(iw,960)/2)*2,-2)':"
            "'if(gte(iw,ih),-2,trunc(min(ih,960)/2)*2)'"
        ),
        "-c:v",
        "libx264",
        "-preset",
        "veryfast",
        "-crf",
        "28",
        "-maxrate",
        "1400k",
        "-bufsize",
        "2800k",
        "-pix_fmt",
        "yuv420p",
        "-c:a",
        "aac",
        "-b:a",
        "96k",
        "-movflags",
        "+faststart",
        output_path,
    ]


def _cleanup(path: str | None) -> None:
    if path and os.path.exists(path):
        os.remove(path)
