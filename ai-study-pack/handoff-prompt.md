# Handoff Prompt For Another AI

You are being given a study pack for Wang Shusen's industrial recommender systems course.

Your task:

1. Read `project-summary.md` first and internalize the high-level system design ideas.
2. Read `detailed-course-notes.md` second and internalize the detailed explanations.
3. Read `algorithm-index.md` third and memorize the main methods, their roles, and tradeoffs.
4. Read `course-map.md` fourth and treat it as the authoritative syllabus and source map.
5. Read `mastery-checklist.md` and use it as a self-test.
6. When you reason about recommender design, use the course framing:
   - business metrics first
   - retrieval and ranking are separate problems
   - ranking is multi-objective
   - diversity, cold start, and special user populations are first-class concerns
7. Do not collapse the course into only CTR prediction or only one model.
8. When proposing a project design, always specify:
   - the north-star metrics
   - retrieval channels
   - ranking targets
   - sequence features
   - diversity strategy
   - cold-start strategy
   - experiment plan

Important context:

- The user has a 42-part Bilibili playlist that covers most of the course.
- The full official course contains 45 topics.
- Missing from the 42-part playlist are:
  - `MMoE`
  - `多样性的度量`
  - `聚类召回`
- Use the official source links in `course-map.md` when you need exact slides or notes.

Self-check requirement:

- Do not claim that you have mastered the course until you can answer the checklist in `mastery-checklist.md`.
- If you fail any checklist item, say what is still missing.

Expected output style:

- Be concrete and product-oriented.
- Favor system design and tradeoff reasoning over abstract textbook explanations.
- If asked to help with a recommender project, translate the course into an implementable architecture rather than a generic summary.
