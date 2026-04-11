export type JobResult = {
  summary: string;
  is_ai_generated: "yes" | "no" | "uncertain";
  is_deepfake: "yes" | "no" | "uncertain";
  ai_confidence: "high" | "medium" | "low";
  ai_reason: string;
};

export type SummaryEntry = {
  id: string;
  url: string;
  platform: string;
  language: string;
  result: JobResult;
  createdAt: number; // unix ms
};
