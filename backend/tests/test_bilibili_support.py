import sys
import tempfile
import unittest
from pathlib import Path


BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from api.routes.captions import TranscriptRequest
from api.routes.summarize import SummarizeRequest
from services.captions import _parse_from_info, _read_subtitle_file
from services.platforms import get_downloader
from services.platforms.base import BasePlatform
from services.platforms.xiaohongshu import XiaoHongShuPlatform
from services.transcript import (
    _languages_differ,
    _render_bilingual_text,
    _render_transcript_text,
)


class PlatformDetectionTests(unittest.TestCase):
    def test_detects_bilibili_desktop_url(self):
        self.assertEqual(
            BasePlatform.detect("https://www.bilibili.com/video/BV11Bd8BPEsb/"),
            "bilibili",
        )

    def test_detects_bilibili_mobile_url(self):
        self.assertEqual(
            BasePlatform.detect("https://m.bilibili.com/video/BV11Bd8BPEsb"),
            "bilibili",
        )

    def test_detects_b23_short_url(self):
        self.assertEqual(BasePlatform.detect("https://b23.tv/abcd1234"), "bilibili")

    def test_detects_xiaohongshu_desktop_url(self):
        self.assertEqual(
            BasePlatform.detect("https://www.xiaohongshu.com/discovery/item/69a53707000000001503b708"),
            "xiaohongshu",
        )

    def test_detects_xhs_short_url(self):
        self.assertEqual(BasePlatform.detect("http://xhslink.com/o/AraalrX99XI"), "xiaohongshu")

    def test_returns_xiaohongshu_downloader(self):
        downloader = get_downloader("http://xhslink.com/o/AraalrX99XI")
        self.assertIsInstance(downloader, XiaoHongShuPlatform)


class RequestValidationTests(unittest.TestCase):
    def test_accepts_bilibili_summary_url(self):
        body = SummarizeRequest(url="https://www.bilibili.com/video/BV11Bd8BPEsb/")
        self.assertEqual(body.url, "https://www.bilibili.com/video/BV11Bd8BPEsb/")

    def test_accepts_bilibili_transcript_url(self):
        body = TranscriptRequest(url="https://www.bilibili.com/video/BV11Bd8BPEsb/")
        self.assertEqual(body.url, "https://www.bilibili.com/video/BV11Bd8BPEsb/")

    def test_accepts_xiaohongshu_summary_short_url(self):
        body = SummarizeRequest(url="http://xhslink.com/o/AraalrX99XI")
        self.assertEqual(body.url, "http://xhslink.com/o/AraalrX99XI")

    def test_accepts_xiaohongshu_transcript_url(self):
        body = TranscriptRequest(url="https://www.xiaohongshu.com/explore/69a53707000000001503b708")
        self.assertEqual(body.url, "https://www.xiaohongshu.com/explore/69a53707000000001503b708")


class CaptionParsingTests(unittest.TestCase):
    def test_prefers_english_subtitle_file_when_multiple_languages_exist(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            Path(tmpdir, "cap.zh-CN.vtt").write_text(
                "WEBVTT\n\n00:00:00.000 --> 00:00:01.000\n\u4f60\u597d\n",
                encoding="utf-8",
            )
            Path(tmpdir, "cap.en.vtt").write_text(
                "WEBVTT\n\n00:00:00.000 --> 00:00:01.000\nhello\n",
                encoding="utf-8",
            )

            segments = _read_subtitle_file(tmpdir)

        self.assertEqual([segment["text"] for segment in segments], ["hello"])

    def test_prefers_english_before_other_languages(self):
        info = {
            "subtitles": {
                "zh-Hans": [{"ext": "json3", "data": ""}],
                "en": [
                    {
                        "ext": "json3",
                        "data": '{"events":[{"tStartMs":0,"dDurationMs":1000,"segs":[{"utf8":"hello"}]}]}',
                    }
                ],
            },
            "automatic_captions": {},
        }

        segments = _parse_from_info(info)

        self.assertEqual([segment["text"] for segment in segments], ["hello"])

    def test_falls_back_to_chinese_when_english_missing(self):
        info = {
            "subtitles": {
                "zh-CN": [
                    {
                        "ext": "vtt",
                        "data": "WEBVTT\n\n00:00:00.000 --> 00:00:01.500\n\u4f60\u597d\u4e16\u754c\n",
                    }
                ]
            },
            "automatic_captions": {},
        }

        segments = _parse_from_info(info)

        self.assertEqual(
            segments,
            [{"start": 0.0, "end": 1.5, "text": "\u4f60\u597d\u4e16\u754c", "x": None, "y": None}],
        )

    def test_falls_back_to_first_available_automatic_caption(self):
        info = {
            "subtitles": {},
            "automatic_captions": {
                "ja": [
                    {
                        "ext": "srt",
                        "data": "1\n00:00:00,000 --> 00:00:02,000\n\u3053\u3093\u306b\u3061\u306f\n",
                    }
                ]
            },
        }

        segments = _parse_from_info(info)

        self.assertEqual(
            segments,
            [{"start": 0.0, "end": 2.0, "text": "\u3053\u3093\u306b\u3061\u306f", "x": None, "y": None}],
        )


class TranscriptFormattingTests(unittest.TestCase):
    def test_treats_zh_variants_as_same_language(self):
        self.assertFalse(_languages_differ("zh-TW", "zh"))

    def test_detects_different_languages_for_bilingual_output(self):
        self.assertTrue(_languages_differ("ja", "en"))

    def test_renders_mono_and_bilingual_transcript_text(self):
        segments = [
            {
                "start": 0.0,
                "end": 1.5,
                "text": "\u4f60\u597d",
                "translated_text": "Hello",
                "x": None,
                "y": None,
            }
        ]

        mono = _render_transcript_text(segments, include_translation=False)
        translated = _render_transcript_text(segments, include_translation=True)
        bilingual = _render_bilingual_text(segments)

        self.assertEqual(mono, "0:00-0:01 \u4f60\u597d")
        self.assertEqual(translated, "0:00-0:01 Hello")
        self.assertEqual(bilingual, "0:00-0:01 \u4f60\u597d\nHello")


if __name__ == "__main__":
    unittest.main()
