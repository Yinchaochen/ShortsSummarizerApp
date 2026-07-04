# Recommender Course AI Study Pack

This folder packages Wang Shusen's industrial recommender systems course into a format that another AI can study deeply.

What is included:

- `course-map.md`: the full topic map of the official course, with section grouping and source links.
- `project-summary.md`: a project-oriented summary of the course, focused on what matters when building a real recommender product.
- `detailed-course-notes.md`: a much more detailed chapter-by-chapter explanation of the course content.
- `algorithm-index.md`: compact algorithm cards for the main models and system strategies in the course.
- `mastery-checklist.md`: a checklist and test bank for deciding whether another AI has really absorbed the course.
- `handoff-prompt.md`: a ready-to-use prompt for another AI.

Important scope notes:

- The user's Bilibili multi-part playlist currently exposes 42 parts.
- The official course source contains 45 topics.
- The 42-part playlist is missing three official topics:
  - `Multi-gate Mixture-of-Experts (MMoE)`
  - `多样性的度量`
  - `聚类召回`
- This pack is based on the official public source repository by Wang Shusen, plus the verified Bilibili multi-part playlist used in this workspace.

Primary sources:

- Official course repo: [wangshusen/RecommenderSystem](https://github.com/wangshusen/RecommenderSystem)
- User playlist root: [BV1FwXrBmEp4](https://www.bilibili.com/video/BV1FwXrBmEp4)

Recommended usage:

1. Give another AI `project-summary.md` first.
2. Then give it `detailed-course-notes.md`.
3. Then give it `algorithm-index.md`.
4. Then give it `course-map.md`.
5. Ask it to self-check against `mastery-checklist.md`.
6. Use `handoff-prompt.md` as the final task prompt.

Important quality note:

- The earlier version of this folder was enough for fast onboarding.
- The current version is designed for much deeper study.
- Even now, no static package can mathematically guarantee "complete mastery".
- What this pack can do is make another AI much more likely to internalize the course correctly and expose gaps via the checklist.
