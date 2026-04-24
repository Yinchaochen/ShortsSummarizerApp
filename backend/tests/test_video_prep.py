import sys
import unittest
from pathlib import Path


BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from services.video_prep import (
    UPLOAD_SIZE_TARGET_MB,
    _build_compressed_path,
    _should_attempt_compression,
    _should_use_compressed_file,
)


class VideoPreparationTests(unittest.TestCase):
    def test_skips_compression_for_small_files(self):
        size = (UPLOAD_SIZE_TARGET_MB - 1) * 1024 * 1024
        self.assertFalse(_should_attempt_compression(size))

    def test_attempts_compression_for_large_files(self):
        size = (UPLOAD_SIZE_TARGET_MB + 5) * 1024 * 1024
        self.assertTrue(_should_attempt_compression(size))

    def test_uses_compressed_file_when_it_hits_target_size(self):
        original = 45 * 1024 * 1024
        compressed = 18 * 1024 * 1024
        self.assertTrue(_should_use_compressed_file(original, compressed))

    def test_uses_compressed_file_when_it_is_significantly_smaller(self):
        original = 60 * 1024 * 1024
        compressed = 50 * 1024 * 1024
        self.assertTrue(_should_use_compressed_file(original, compressed))

    def test_rejects_compressed_file_when_savings_are_too_small(self):
        original = 60 * 1024 * 1024
        compressed = 56 * 1024 * 1024
        self.assertFalse(_should_use_compressed_file(original, compressed))

    def test_builds_sibling_compressed_path(self):
        compressed = _build_compressed_path(r"E:\tmp\clip.mp4")
        self.assertEqual(compressed, r"E:\tmp\clip_gemini.mp4")


if __name__ == "__main__":
    unittest.main()
