# Spec #9 — Repo-root AGENTS.md — agent onboarding facts

Repo: CherokeeLanguage/Animals (single-repo scope).
Source of requirements: developer directive "Create or update `AGENTS.md` for this repository" + post-hoc "create spec for the pending changes then approved for pr".

## Problem

The repo lacked a repo-root `AGENTS.md`: future OpenCode sessions got no repo-specific onboarding (the existing instruction surface, `.opencode/AGENTS.md`, is the pointer-only deck file in a read-only checkout). Agents ramping up cold had to rediscover the dual-codebase tree, the nonexistent build/test command, the generated-data pipeline, and the stale readmes by direct investigation.

## Scope

One new file: `AGENTS.md` at the repo root — fact-only onboarding (no normative pipeline rules; those remain owned by the floor via `.opencode/AGENTS.md`). Branch: `feature/agents-md-onboarding` (commit `2bd4a3f`, pushed to origin).

## Requirements

- R-1: `AGENTS.md` exists at the repo root and states only repo-specific, verified facts; it does not duplicate normative pipeline/authorization/merge rules owned by the floor.
- R-2: `.opencode/**` (read-only deck checkout) is untouched by this unit.

## Success criteria

| ID | Criterion | Class | Instrument |
|---|---|---|---|
| SC-1 | Every factual claim in `AGENTS.md` is verifiably true against the repo (paths, versions, commands, counts) | structural | fresh-context review executed per claim against the tree: Godot 3.3/GLES2 in project.godot; CodeQL-only CI; Rider-stub gradle roots; generator rmtree paths `assets/challenges/{audio,images}`; generated header in ChallengeData.gd |
| SC-2 | The commit touches only `AGENTS.md`; `.opencode/**` unmodified | structural | `git show --stat 2bd4a3f`; platform confirmation that `.opencode/AGENTS.md` (the pointer copy) is unchanged |

## Out of scope

Rewriting the stale `README.md`/`readme.md`; CI; the deck checkout.

## Approach

Short-path implementation: single new file authored from verified investigation; facts re-checked by a dispatch-based review loop (round-1 finding on a line-count claim + rmtree path casing remediated; round-2 PASS zero findings).

## Validation record

- Round 1: FAIL (1 finding: core/src line count stated ~6k, actual ~10k; plus the author's own discovery of rmtree path casing — capitalized `Audio`/`Pictures` vs actual lowercase `audio`/`images`) — both remediated in place, commit amended.
- Round 2: PASS (zero findings) — fresh-context reviewer re-counted the tree, verified the ~10k claim and that the commit touches only `AGENTS.md`.

Byline: 🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
