from __future__ import annotations

import json
import os
from typing import Any

from google import genai
from google.genai import types

from services.captions import extract_captions
from services.gemini import GEMINI_MODELS


_DETECTION_SAMPLE_SEGMENTS = 40
_DETECTION_SAMPLE_CHARS = 4000
_TRANSLATION_MAX_SEGMENTS = 80
_TRANSLATION_MAX_CHARS = 9000


def build_transcript(url: str, target_language: str = "en") -> dict[str, Any]:
    segments = [
        {
            "start": float(segment["start"]),
            "end": float(segment["end"]),
            "text": str(segment["text"]).strip(),
            "x": segment.get("x"),
            "y": segment.get("y"),
        }
        for segment in extract_captions(url)
        if str(segment.get("text", "")).strip()
        and segment.get("x") is None
        and segment.get("y") is None
    ]

    if not segments:
        raise ValueError("No transcript text found for this URL.")

    source_language = _detect_language(segments)
    is_bilingual = _languages_differ(source_language["code"], target_language)

    translated_segments: list[dict[str, Any]] = []
    if is_bilingual:
        translated_segments = _translate_segments(segments, target_language)

    merged_segments = _merge_segments(segments, translated_segments)
    original_text = _render_transcript_text(merged_segments, include_translation=False)
    translated_text = (
        _render_transcript_text(merged_segments, include_translation=True)
        if is_bilingual
        else None
    )
    display_text = _render_bilingual_text(merged_segments) if is_bilingual else original_text

    return {
        "source_language": source_language,
        "target_language": {
            "code": target_language,
            "label": target_language,
        },
        "is_bilingual": is_bilingual,
        "segments": merged_segments,
        "original_text": original_text,
        "translated_text": translated_text,
        "display_text": display_text,
    }


def _get_client() -> genai.Client:
    api_key = os.environ.get("GOOGLE_API_KEY", "")
    if not api_key:
        raise EnvironmentError("GOOGLE_API_KEY not set")
    return genai.Client(api_key=api_key)


def _call_gemini_json(prompt: str) -> dict[str, Any]:
    client = _get_client()
    last_error: str | None = None

    for model_name in GEMINI_MODELS:
        try:
            response = client.models.generate_content(
                model=model_name,
                contents=prompt,
                config=types.GenerateContentConfig(
                    response_mime_type="application/json",
                ),
            )
            return _parse_json_response(response.text)
        except Exception as exc:  # pragma: no cover - exercised via fallback behavior
            last_error = str(exc)

    raise RuntimeError(f"All Gemini models failed: {last_error}")


def _parse_json_response(text: str) -> dict[str, Any]:
    cleaned = text.strip()
    if cleaned.startswith("```"):
        cleaned = cleaned.split("\n", 1)[-1]
        cleaned = cleaned.rsplit("```", 1)[0]
    return json.loads(cleaned)


def _detect_language(segments: list[dict[str, Any]]) -> dict[str, str]:
    sample_lines: list[str] = []
    total_chars = 0

    for segment in segments[:_DETECTION_SAMPLE_SEGMENTS]:
        text = segment["text"]
        if not text:
            continue
        sample_lines.append(text)
        total_chars += len(text)
        if total_chars >= _DETECTION_SAMPLE_CHARS:
            break

    prompt = "\n".join(
        [
            "Detect the primary language of this subtitle transcript.",
            'Return only valid JSON with keys "language_code" and "language_label".',
            'Use concise BCP-47-style codes when obvious, such as "en", "zh", "zh-TW", "ja", "ko", or "es".',
            'If uncertain, use "unknown" for both fields.',
            "",
            "Transcript sample:",
            "\n".join(sample_lines),
        ]
    )

    data = _call_gemini_json(prompt)
    code = str(data.get("language_code") or "unknown").strip() or "unknown"
    label = str(data.get("language_label") or code).strip() or code
    return {"code": code, "label": label}


def _translate_segments(
    segments: list[dict[str, Any]],
    target_language: str,
) -> list[dict[str, Any]]:
    translated: list[dict[str, Any]] = []

    for chunk in _chunk_segments(segments):
        payload = [
            {"id": index, "text": segment["text"]}
            for index, segment in enumerate(chunk)
        ]
        prompt = "\n".join(
            [
                f'Translate every subtitle segment into target language code "{target_language}".',
                "Keep the meaning and tone, but stay concise like subtitles.",
                "Do not merge or split entries.",
                "Preserve every id exactly.",
                'Return only valid JSON in the shape {"translations":[{"id":0,"translated_text":"..."}]}.',
                'If a line is already in the target language, keep it unchanged.',
                "",
                "Input JSON:",
                json.dumps(payload, ensure_ascii=False),
            ]
        )

        data = _call_gemini_json(prompt)
        items = data.get("translations")
        if not isinstance(items, list):
            raise RuntimeError("Transcript translation failed: invalid response payload.")

        by_id: dict[int, str] = {}
        for item in items:
            try:
                idx = int(item["id"])
                translated_text = str(item["translated_text"]).strip()
            except Exception as exc:
                raise RuntimeError("Transcript translation failed: malformed segment.") from exc
            by_id[idx] = translated_text

        if len(by_id) != len(chunk):
            raise RuntimeError("Transcript translation failed: segment count mismatch.")

        for index, segment in enumerate(chunk):
            translated.append(
                {
                    **segment,
                    "translated_text": by_id.get(index, segment["text"]),
                }
            )

    return translated


def _chunk_segments(segments: list[dict[str, Any]]) -> list[list[dict[str, Any]]]:
    chunks: list[list[dict[str, Any]]] = []
    current: list[dict[str, Any]] = []
    current_chars = 0

    for segment in segments:
        text_len = len(segment["text"])
        would_overflow = (
            current
            and (
                len(current) >= _TRANSLATION_MAX_SEGMENTS
                or current_chars + text_len > _TRANSLATION_MAX_CHARS
            )
        )
        if would_overflow:
            chunks.append(current)
            current = []
            current_chars = 0

        current.append(segment)
        current_chars += text_len

    if current:
        chunks.append(current)

    return chunks


def _languages_differ(source_language: str, target_language: str) -> bool:
    return _normalize_language_code(source_language) != _normalize_language_code(target_language)


def _normalize_language_code(code: str) -> str:
    normalized = (code or "").strip().lower()
    if not normalized:
        return "unknown"
    if normalized.startswith("zh"):
        return "zh"
    return normalized.split("-", 1)[0]


def _merge_segments(
    original_segments: list[dict[str, Any]],
    translated_segments: list[dict[str, Any]],
) -> list[dict[str, Any]]:
    if not translated_segments:
        return [dict(segment, translated_text=None) for segment in original_segments]

    if len(original_segments) != len(translated_segments):
        raise RuntimeError("Transcript translation failed: merged segment count mismatch.")

    merged: list[dict[str, Any]] = []
    for original, translated in zip(original_segments, translated_segments):
        merged.append(
            {
                **original,
                "translated_text": translated.get("translated_text"),
            }
        )
    return merged


def _render_transcript_text(
    segments: list[dict[str, Any]],
    *,
    include_translation: bool,
) -> str:
    lines: list[str] = []
    for segment in segments:
        text = segment.get("translated_text") if include_translation else segment["text"]
        if not text:
            continue
        lines.append(f"{_format_time_range(segment['start'], segment['end'])} {text}")
    return "\n".join(lines)


def _render_bilingual_text(segments: list[dict[str, Any]]) -> str:
    blocks: list[str] = []
    for segment in segments:
        translated = segment.get("translated_text")
        source_line = f"{_format_time_range(segment['start'], segment['end'])} {segment['text']}"
        if translated:
            blocks.append(f"{source_line}\n{translated}")
        else:
            blocks.append(source_line)
    return "\n\n".join(blocks)


def _format_time_range(start: float, end: float) -> str:
    return f"{_format_timestamp(start)}-{_format_timestamp(end)}"


def _format_timestamp(seconds: float) -> str:
    total_seconds = max(0, int(seconds))
    hours, remainder = divmod(total_seconds, 3600)
    minutes, secs = divmod(remainder, 60)
    if hours:
        return f"{hours}:{minutes:02d}:{secs:02d}"
    return f"{minutes}:{secs:02d}"
