import { supabase } from "./supabase";

const API_BASE = "http://127.0.0.1:8000"; // Update to deployed URL in production

async function getAuthHeader(): Promise<Record<string, string>> {
  const { data } = await supabase.auth.getSession();
  const token = data.session?.access_token;
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function submitSummarize(url: string, language: string): Promise<string> {
  const headers = await getAuthHeader();
  const res = await fetch(`${API_BASE}/api/summarize`, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers },
    body: JSON.stringify({ url, language }),
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.detail || "Failed to submit");
  }
  const { job_id } = await res.json();
  return job_id;
}

export async function pollJob(jobId: string): Promise<{ state: string; label?: string; result?: string; detail?: string }> {
  const res = await fetch(`${API_BASE}/api/job/${jobId}`);
  return res.json();
}

export async function getUsage(): Promise<{ free_limit: number; used: number; remaining: number }> {
  const headers = await getAuthHeader();
  const res = await fetch(`${API_BASE}/api/usage`, { headers });
  return res.json();
}
