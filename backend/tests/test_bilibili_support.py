import sys
import tempfile
import unittest
from pathlib import Path


BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from api.routes.summarize import SummarizeRequest
from services.captions import _parse_from_info, _read_subtitle_file
from services.platforms.base import BasePlatform


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


class SummarizeRequestTests(unittest.TestCase):
    def test_accepts_bilibili_url(self):
        body = SummarizeRequest(url="https://www.bilibili.com/video/BV11Bd8BPEsb/")
        self.assertEqual(body.url, "https://www.bilibili.com/video/BV11Bd8BPEsb/")


class CaptionParsingTests(unittest.TestCase):
    def test_prefers_english_subtitle_file_when_multiple_languages_exist(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            Path(tmpdir, "cap.zh-CN.vtt").write_text(
                "WEBVTT\n\n00:00:00.000 --> 00:00:01.000\n你好\n",
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
                        "data": "WEBVTT\n\n00:00:00.000 --> 00:00:01.500\n你好世界\n",
                    }
                ]
            },
            "automatic_captions": {},
        }

        segments = _parse_from_info(info)

        self.assertEqual(
            segments,
            [{"start": 0.0, "end": 1.5, "text": "你好世界", "x": None, "y": None}],
        )

    def test_falls_back_to_first_available_automatic_caption(self):
        info = {
            "subtitles": {},
            "automatic_captions": {
                "ja": [
                    {
                        "ext": "srt",
                        "data": "1\n00:00:00,000 --> 00:00:02,000\nこんにちは\n",
                    }
                ]
            },
        }

        segments = _parse_from_info(info)

        self.assertEqual(
            segments,
            [{"start": 0.0, "end": 2.0, "text": "こんにちは", "x": None, "y": None}],
        )


if __name__ == "__main__":
    unittest.main()
