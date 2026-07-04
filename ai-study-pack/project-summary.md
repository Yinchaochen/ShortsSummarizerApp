# Project-Oriented Summary

This summary is written for AI assistants or engineers who need to use the course in a real product context.

## One-sentence view

Industrial recommender systems are not about finding one best model. They are about building a layered decision system around the right business goals: `retrieval -> ranking -> reranking -> experimentation`.

## The central ideas

### 1. Optimize business goals, not only CTR

The course treats `DAU`, `retention`, `time spent`, and platform-specific value as the true north-star metrics. CTR and interaction rates matter, but they are often only supporting metrics.

Practical takeaway:

- Never define a recommender project only around click prediction.
- At minimum, track retention and time-related metrics together with click and interaction metrics.

### 2. Retrieval is a multi-channel system

Retrieval is not one model. Industrial systems combine many retrieval channels, such as:

- `item-to-item` methods like `ItemCF` and `Swing`
- `user-to-item` embedding retrieval such as `two-tower`
- tree or path based methods such as `Deep Retrieval`
- rule-driven channels such as freshness pools, geo pools, new-user pools, and author pools

Practical takeaway:

- Design retrieval as a portfolio of channels.
- Each channel should have a purpose, target population, and quota.

### 3. Ranking is multi-objective

Ranking should estimate several outcomes jointly, such as:

- click-through rate
- like rate
- collect/favorite rate
- share rate
- watch time or completion-related signals

These are later fused into a final score.

Practical takeaway:

- Use multi-task prediction or equivalent multi-objective scoring.
- For video products, do not rely only on click. Watch-time modeling is essential.

### 4. Features often matter more than fancy models

The course repeatedly highlights the value of:

- user profile features
- item profile features
- recent aggregated user statistics
- recent aggregated item statistics
- feature crosses
- sequence features

Practical takeaway:

- Before reaching for a more complex architecture, improve feature coverage and feature freshness.
- Keep feature definitions aligned with product behavior and business logic.

### 5. Sequence modeling captures current intent

Static user embedding is not enough. Recent behavior often reveals short-term intent better than long-term profile.

Key models:

- `DIN`: attention over recent interactions
- `SIM`: scalable retrieval of the most relevant historical behaviors before attention

Practical takeaway:

- If your project has session-like or feed-like behavior, sequence features should be in v1 or very early v2.

### 6. Diversity is not optional

If the system only optimizes interest score, users often get repetitive content. Short-term metrics may look good while long-term satisfaction drops.

Common strategies in the course:

- `MMR`
- `DPP`
- rule-based spacing and category breakup
- sliding-window reranking

Practical takeaway:

- Add diversity explicitly, especially in final reranking.
- Evaluate diversity as part of retention strategy, not as decoration.

### 7. Cold start needs separate logic

New items lack interaction statistics and stable embeddings. On UGC platforms, cold start also affects creator willingness to keep publishing.

Practical takeaway:

- Treat new-item distribution as a dedicated subsystem.
- Separate user-side and creator-side metrics.
- Consider traffic allocation, freshness pools, and early exposure protection.

### 8. Special populations need special handling

New users and low-activity users do not behave like established users. Models trained on the majority population often underperform badly on them.

Practical takeaway:

- Build special pools, special ranking strategies, or even special models for fragile populations.
- Protect retention-heavy groups instead of forcing one global strategy.

### 9. Interaction behaviors can be strategic signals

The course does not treat `follow`, `share`, and `comment` as just extra labels. They are strategic behaviors that affect future retention and ecosystem growth.

Practical takeaway:

- If a downstream action improves long-term product value, consider optimizing for it directly.
- Example: if following authors raises retention, the system can intentionally encourage follows for users with low follow counts.

## A reusable architecture template

For many real projects, a strong first architecture inspired by this course is:

1. Multi-channel retrieval
2. Lightweight coarse ranker
3. Multi-objective fine ranker
4. Final reranking for diversity, freshness, and policy constraints
5. Online A/B testing with retention-aware evaluation

## What to remember when building your own project

- Define the north-star metrics first.
- Start with clear retrieval channel design.
- Build multi-objective ranking early.
- Model recent behavior, not only user profile.
- Add diversity and cold-start handling on purpose.
- Protect new and weakly observed users.
- Trust online experiments more than pretty offline metrics.

## The most important sentence from the whole course

Industrial recommendation is a system optimization problem shaped by product goals, not a single-model leaderboard problem.
