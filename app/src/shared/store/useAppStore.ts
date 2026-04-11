import { create } from "zustand";
import { SummaryEntry } from "../../features/summarizer/types";

type AppState = {
  // ── Auth ──────────────────────────────────────────────────────────────────
  userId: string | null;
  setUserId: (id: string | null) => void;

  // ── Usage (shown in header / paywall) ─────────────────────────────────────
  usageRemaining: number | null;
  setUsageRemaining: (n: number | null) => void;

  // ── Summary history (future history screen) ────────────────────────────────
  recentSummaries: SummaryEntry[];
  addSummary: (entry: Omit<SummaryEntry, "createdAt">) => void;
  clearSummaries: () => void;
};

export const useAppStore = create<AppState>((set) => ({
  userId: null,
  setUserId: (id) => set({ userId: id }),

  usageRemaining: null,
  setUsageRemaining: (n) => set({ usageRemaining: n }),

  recentSummaries: [],
  addSummary: (entry) =>
    set((state) => ({
      recentSummaries: [
        { ...entry, createdAt: Date.now() },
        ...state.recentSummaries,
      ].slice(0, 50),
    })),
  clearSummaries: () => set({ recentSummaries: [] }),
}));
