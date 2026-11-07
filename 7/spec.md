# Spec #7 — Repo tooling: Rider workspace files + editor import-metadata completion

Repo: CherokeeLanguage/Animals (single-repo scope). Trunk: `master`.
Source of requirements: developer directive "File PR to cover all pending changes" (resolves the disposition of this unit as: commit as-is, nothing reverted) + the working tree's staged/untracked state.

## Problem

The remaining pending changes are tooling, deliberately staged but never committed: JetBrains Rider workspace files at the repo root, the untracked `Animals.gd` Rider launcher, a `.gitignore` addition, and ~2300 untracked Godot `.import` editor-metadata files under `assets/`. The tree's own committed convention (commit `c4e4861`, `assets/audio-effects/*`) tracks `.import` files alongside assets, so the challenge sets' omission reads as drift, not policy.

## Scope

| Surface | Pending change |
|---|---|
| `build.gradle`, `settings.gradle` | IDEA-module Rider workspace stubs (build nothing) |
| `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat` | Gradle 7.4 wrapper |
| `gradle.properties` | deleted (Rider-era workspace JVM/daemon settings) |
| `Animals.gd` (untracked) | 7-line root Node script so Rider opens the project as one workspace |
| `.gitignore` | += `/.opencode/` |
| `assets/**/*.import` | ~2300 untracked Godot import-metadata files (audio + images under `assets/challenges/`) |

## Requirements

- R-1: All pending tooling edits above are committed as they stand — no reversion, no further edits.
- R-2: `Animals.gd` and the untracked `.import` metadata are committed; no additional untracked files are added to this unit (`.issues/` and `.mono/` stay untracked).
- R-3: The branch carrying this work also carries the #5 and #6 units as their own distinct commits (`scenes/menu/*`, `scenes/EffectAudio.tscn`, `addons/smart_audio_stream/`, `project.godot`, `classes/ChallengeData.gd`, `Assets.Source/challenges/create_challenge_data.py` are owned by those specs) — this unit's commit touches only its scope table's surfaces.

## Success criteria

| ID | Criterion | Class | Instrument |
|---|---|---|---|
| SC-1 | `gradle-wrapper.properties` names distribution Gradle 7.4; root `build.gradle` contains only the `idea` plugin block + Rider comment | structural | grep the two files |
| SC-2 | Scope-table files tracked at expected content; no remaining untracked `.import` under `assets/` | structural | `git status --porcelain` + `git ls-files` spot checks |
| SC-3 | No functional build/export behavior changes; Godot export presets untouched; nothing outside the scope table modified by this unit | structural | `git diff` review of the unit commit |

## Out of scope

Audio behavior (#5), challenge data content (#6), Android export configuration, installing a Godot Android build template, CI.

## Approach

Commit the pending tooling edits verbatim as one commit; verify a clean tree afterward.

Byline: 🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
