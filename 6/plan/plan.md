# Plan #6 — Regenerated TTS challenge data + generator fixes

Branch: feature/5-pending-changes. Spec: .issues/6/spec.md. Deliverable is the pending diff committed verbatim.

## Items
1. **GREEN** (pre-existing change): run SC-1 instrument (grep header; sample mp3s stems vs `assets/challenges/audio/`).
2. Stage exactly: `Assets.Source/challenges/create_challenge_data.py`, `classes/ChallengeData.gd`, and all `assets/challenges/**/*.import` untracked files; review staged summary; commit (refs #6).
3. Verify SC-2: `git status --porcelain` shows no untracked files under assets/challenges after commit.
4. SC-3 (behavioral, generator idempotence): execute only if python deps (PIL, pydub) are available — run in a temp copy of the repo to avoid rmtree-side effects on the real tree; otherwise record unexecuted with reason.

## Dependencies
None relative to #5/#7 (order 5→6→7 on the branch).

## Traceability / documentation sources
Working-tree diff vs HEAD at a33a73b; specs 5/6/7; create_challenge_data.py.

## Edge cases
None beyond the temp-copy execution guard above.
