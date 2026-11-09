# Plan — Animals#11: itch.io deploy-target conversion (repo-local push tooling)

Spec: `.issues/11/spec.md` — validated, rounds 3–4 PASS (`.issues/11/audit/validation.yaml`).
Authorization: developer 2026-11-09, "#11 approved for implementation" (carries plan into implement; the PR boundary remains its own gate).

## Approach

Mirror the BoundPronouns pattern (`itch-push.sh`, `itch.conf.example`, `.gitignore` in `/home/muksihs/git/BoundPronouns/` — read 2026-11-09), with the one spec-pinned adaptation:

- BoundPronouns' script locates artifacts by globbing its own build outputs (`desktop/build/libs/BoundPronouns-*.jar`, `android/build/outputs/apk/release/*-release.apk`). Animals has no build surface (spec Scope: legacy libGDX is reference-only; the gradle roots are IDE stubs) and R-2/SC-2 require artifact paths as operator config. The artifact-locating section is therefore replaced by two config variables (`ITCH_JAR`, `ITCH_APK`) read from `itch.conf`, carried as `REPLACE_ME` placeholders in the tracked `itch.conf.example`, with file-existence checks in the script replacing the build globs.

Everything else mirrors the pattern: conf presence check, `: "${VAR:?…}"` required-var guards, `REPLACE_ME` equality guard, butler resolution PATH→vendored `.tools/butler/butler` with the vendor/login instruction block, per-channel `butler push` + `butler status` receipts, `set -euo pipefail`, `cd "$(dirname "$0")"`.

Channel mapping (source of truth: the paired spec CherokeeLessons.com#22 channel table):

| Channel | conf var | operator fills with |
|---|---|---|
| `win-linux-mac` | `ITCH_JAR` | the staged desktop jar — the `dist/` copies per the spec's deploy-source facts |
| `android` | `ITCH_APK` | the staged release APK |

Push target: `ITCH_USER=cherokee-language`, `ITCH_GAME=cherokee-language-animals` → `cherokee-language/cherokee-language-animals` (R-3/SC-3). No `--userversion` anywhere (R-4/SC-4) — butler's auto-numbering stands.

Tracked-file hygiene: no exact artifact filenames (`Animals-5.12*`) appear in tracked files — comments reference the deploy source generically ("the staged `dist/` copies"); the exact mapping lives in this plan, the spec, and the operator's local `itch.conf`.

Not executed in this work: the script is created and syntax-checked, never run (the paired itch project does not exist yet; the spec performs no production push). `itch.conf` itself is operator config (`cp itch.conf.example itch.conf`, filled at deploy time) — not an item deliverable.

## Items (execution order)

### Item 1 — gitignore guard for local-only deploy state (SC-1)
- Deliverable: `.gitignore` gains `/itch.conf` and `/.tools/butler/` (BoundPronouns `.gitignore` lines 68 and 10).
- RED: `git check-ignore itch.conf` exits nonzero on the pre-work tree — verified 2026-11-09 (the 69-line file has no itch/tools entries); SC-1's "itch.conf is gitignored" check fails now.
- GREEN: both rules added; the local config and a vendored butler binary can never enter history.
- Instrument: `git check-ignore -v itch.conf .tools/butler/` prints a matching rule line for each path.

### Item 2 — `itch.conf.example`: bound target + REPLACE_ME artifact paths (SC-1 file existence; SC-2 config placeholders; SC-3 target binding)
- Deliverable: tracked `itch.conf.example`, content per the pattern's example — header comment (purpose, REPLACE_ME instruction, no-secrets note: butler keeps credentials in its own store via `butler login`) with the Animals values:
  - `ITCH_USER=cherokee-language`
  - `ITCH_GAME=cherokee-language-animals`
  - `ITCH_JAR=REPLACE_ME` (comment: path to the desktop jar, e.g. the staged `dist/` copy)
  - `ITCH_APK=REPLACE_ME` (comment: path to the release APK, e.g. the staged `dist/` copy)
- Deviation note: the BoundPronouns example fills real values for the target (no REPLACE_ME there); this repo binds the same real target — SC-3 requires the binding in script *and* config — while the artifact paths stay REPLACE_ME per SC-2.
- RED: `test -f itch.conf.example` fails on the pre-work tree (file absent).
- GREEN: the file exists with the four assignments above.
- Instrument: `grep -n` reads confirming each of the four assignments; `grep -c 'Animals-5.12' itch.conf.example` returns 0 (no hardcoded artifact filenames in the tracked file).

### Item 3 — `itch-push.sh`: pattern script with config-driven artifact section (SC-1 script guards + file existence; SC-2 channel set; SC-3 target composition; SC-4 no version claims)
- Deliverable: executable `itch-push.sh`, section structure mirroring the pattern's order:
  1. Shebang + header comment: purpose (push the published Animals artifacts — the existing APK/JAR deploy source — to itch.io via butler), prerequisites (butler on PATH or vendored at `.tools/butler/butler`, with the vendor curl/unzip instructions; `butler login`; `itch.conf` filled with ITCH_USER/ITCH_GAME/ITCH_JAR/ITCH_APK).
  2. `set -euo pipefail`; `cd "$(dirname "$0")"`.
  3. Conf load: presence check (error hint: `cp itch.conf.example itch.conf`, then fill), `source itch.conf`.
  4. Required-var guards `: "${ITCH_USER:?…}"`, `: "${ITCH_GAME:?…}"`, `: "${ITCH_JAR:?…}"`, `: "${ITCH_APK:?…}"`; the REPLACE_ME equality guard over all four, pattern error text.
  5. Butler resolution: `command -v butler` → else executable `.tools/butler/butler` → else the pattern's vendor+login error block.
  6. Artifact checks (replaces the pattern's build-glob section): `[ -f "${ITCH_JAR}" ]` and `[ -f "${ITCH_APK}" ]`, errors naming the conf variable and pointing at the staged `dist/` copies.
  7. Channels: `JAR_CHANNEL="${ITCH_USER}/${ITCH_GAME}:win-linux-mac"`, `APK_CHANNEL="${ITCH_USER}/${ITCH_GAME}:android"`.
  8. Pushes: plain `"${BUTLER}" push "${ITCH_JAR}" "${JAR_CHANNEL}"` and the APK analog — no `--userversion`; then `butler status` receipts per channel; "Done."
- RED: `test -f itch-push.sh` fails on the pre-work tree (file absent).
- GREEN: the file exists, is executable, and carries sections 1–8.
- Instrument: `bash -n itch-push.sh` (parses clean); `test -x itch-push.sh`; `grep -n 'REPLACE_ME' itch-push.sh` (guard present); `grep -n 'win-linux-mac'` and `grep -n ':android'` each match; `grep -c -- '--userversion' itch-push.sh` returns 0; `grep -c 'Animals-5.12' itch-push.sh` returns 0.

## Ordering

1 → 2 → 3. Item 3's script references the conf/example flow items 1–2 establish; no item depends on a later item's RED or GREEN. All four SCs' instruments are re-runnable against the final tree; the verify stage re-runs them per the spec's instrument column.

## Coverage map

- SC-1 ← items 1, 2, 3 (gitignore clause: item 1; file existence + guards: items 2–3)
- SC-2 ← items 2, 3 (config placeholders: item 2; channel set + no hardcoded paths: item 3)
- SC-3 ← items 2, 3 (conf binding: item 2; script composition: item 3)
- SC-4 ← item 3

No item lacks an SC trace. The item/SC split is file-grain because each SC's check spans the final two-file state; per-SC micro-edits inside one new file would not be independently verifiable or revertible units.

## Out of scope (carried from the spec)

No push executed; no `itch.conf` created or filled (operator step at deploy time); no paired CherokeeLessons.com#22 work; no Godot-port mapping; no artifact rebuild. Affected file set = `.gitignore`, `itch.conf.example`, `itch-push.sh` — nothing else.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
