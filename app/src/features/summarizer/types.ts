export type JobResult = {
  summary: string;
  is_ai_generated: "yes" | "no" | "uncertain";
  is_deepfake: "yes" | "no" | "uncertain";
  ai_confidence: "high" | "medium" | "low";
  ai_reason: string;
};

export type TranscriptSegment = {
  start: number;
  end: number;
  text: string;
  translated_text: string | null;
  x: number | null;
  y: number | null;
};

export type TranscriptLanguage = {
  code: string;
  label: string;
};

export type TranscriptResult = {
  source_language: TranscriptLanguage;
  target_language: TranscriptLanguage;
  is_bilingual: boolean;
  segments: TranscriptSegment[];
  original_text: string;
  translated_text: string | null;
  display_text: string;
};

export type SummaryEntry = {
  id: string;
  url: string;
  platform: string;
  language: string;
  result: JobResult;
  createdAt: number; // unix ms
};
