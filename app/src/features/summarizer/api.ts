import { supabase } from "../../shared/lib/supabase";
import { JobResult } from "./types";

const API_BASE = process.env.EXPO_PUBLIC_API_URL ?? "https://shortssummarizer.up.railway.app";
const API_V1 = `${API_BASE}/api/v1`;

// ─── Error type ───────────────────────────────────────────────────────────────

export class ApiError extends Error {
  constructor(
    public readonly code: string,
    message: string,
    public readonly status: number = 400,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

async function getAuthHeader(): Promise<Record<string, string>> {
  const { data } = await supabase.auth.getSession();
  const token = data.session?.access_token;
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (res.ok) return res.json() as Promise<T>;
  let code = "ERROR";
  let message = `Request failed (${res.status})`;
  try {
    const body = await res.json();
    // Structured error: { detail: { code, message } }
    if (body?.detail?.code) {
      code = body.detail.code;
      message = body.detail.message ?? message;
    } else if (typeof body?.detail === "string") {
      // Legacy plain-string detail
      code = body.detail;
      message = body.detail;
    }
  } catch {
    // Body wasn't JSON — use defaults
  }
  throw new ApiError(code, message, res.status);
}

// ─── API calls ────────────────────────────────────────────────────────────────

export async function submitSummarize(url: string, language: string): Promise<string> {
  const headers = await getAuthHeader();
  const res = await fetch(`${API_V1}/summarize`, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers },
    body: JSON.stringify({ url, language }),
  });
  const data = await handleResponse<{ job_id: string }>(res);
  return data.job_id;
}

export async function pollJob(jobId: string): Promise<{
  state: string;
  label?: string;
  result?: JobResult;
  code?: string;
  detail?: string;
}> {
  const res = await fetch(`${API_V1}/job/${jobId}`);
  if (!res.ok) throw new ApiError("NETWORK_ERROR", "Failed to poll job status");
  return res.json();
}

export async function getUsage(): Promise<{
  free_limit: number;
  used: number;
  remaining: number;
}> {
  const headers = await getAuthHeader();
  const res = await fetch(`${API_V1}/usage`, { headers });
  return handleResponse(res);
}
