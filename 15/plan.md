# Plan — Animals#15: instructions-screen display fix + redeploy

Spec: `.issues/15/spec.md` — validated PASS round 3 (`.issues/15/audit/validation.yaml`). Authorization: the developer's work order "add in the display fix" (2026-11-09) — carries the item through plan → implement; the hotfix line's established flow (direct commit + push on `hotfix/5.12`; no master-targeting PR — the line is tag-based and never merges forward).

## Approach statement

The verify-first shape of #13 carries over: code fix → rebuild → capture-verified acceptance → re-push → record. Surface split (each tool lives where it lives): the CODE + build run in the `/tmp/opencode/hotfix-display` worktree (at `hotfix/5.12` `36720a1`, preps already run, current build present) — the rebuild's `JAVA_HOME` points at the main checkout's vendored JDK 8 (`/home/muksihs/git/Animals/.tools/jdk`, local untracked state) and its built jar is copied to the main checkout's `dist/Animals-5.12.jar`; the re-push runs from the MAIN checkout (`itch-push.sh` + `itch.conf` + vendored butler live there — they are master-side, not hotfix/5.12 content). The acceptance state is as-shipped: the operator prefs store carries `force-television-mode=true`, so both captures run with TV text — the binding constraint the spec requires.

## Items (execution order)

### Item 1 — SC-4: the bounded code fix
- Deliverable: `ScreenInstructions.java`: `fontSize` 64→44; `instructions.setFillParent(true)` in `initScreen()`, replacing the manual `setWidth(fullZoneBox.width)/setHeight(fullZoneBox.height)`.
- RED: the built jar renders wrapped rows clipping both frame edges mid-word and a bottom-cut footer (three captures on record; the wrap boundary measured ~110–118% of frame width) — the pre-fix state.
- GREEN: the table is stage-sized (the label's wrap constraint == the frame) and the TV text's ~14 wrapped rows at 44px fit inside 1080 (~924 px).
- Instrument: `git show hotfix/5.12 --stat` post-commit — one file, ~2–6 edited lines.

### Item 2 — SC-1 + SC-2: rebuild + both-geometry acceptance
- Deliverable: `desktop:dist` rebuild in the worktree (preps already run; the idempotent prep chain re-runs harmlessly), with `JAVA_HOME=/home/muksihs/git/Animals/.tools/jdk`; captures of the instructions screen at 1280×720 AND 1920×1043 under the machine's real prefs state (TV mode).
- RED: the pre-fix captures show edge-cut rows + bottom-cut footer.
- GREEN: fresh captures show zero edge-cut rows and an in-frame footer.
- Instrument: the established Xvfb driver + dispatched image inspection on both fresh captures; `unzip -l` profile on the jar (3,560 = 2,274 + 28 + 1,258; 457 challenge images incl. `ani.png`; 421 audio).

### Item 3 — SC-3: re-push + page verification
- Deliverable: the rebuilt jar staged at the MAIN checkout's `dist/Animals-5.12.jar` (superseding the #13 build — its bytes remain in butler's history and the site checkout); `itch-push.sh` executes from the main checkout (its tooling: `itch.conf`, vendored butler); the page fetch.
- RED: the page currently lists the #13 jar (56 MB, Version 1 — pre-fix build).
- GREEN: the page lists the same filename with a new butler build and the re-pushed jar's size; the android no-op; receipts auto-numbered.
- Instrument: HTTP fetch; butler output.

### Item 4 — SC-5: sibling record note
- Deliverable: the #22 win-linux-mac row's deployment note gains the display-fix redeployment reference (in-place revision, store-synced).
- Instrument: read of the sibling record post-edit.

## Ordering

1 → 2 → 3 → 4; capture acceptance before the re-push; no item depends on a later one.

## Coverage map

SC-4←1; SC-1+SC-2←2; SC-3←3; SC-5←4. No SC uncovered; no item without an SC trace.

## Out of scope

As the spec states: prefs untouched, other screens untouched, no forward merge, no redirect.

## PR stage

No master-targeting PR exists to open: the change lives on `hotfix/5.12` (per the line's flow, pushed directly; unlike #13 there is no trunk-side delta). The review surface is the branch's commit + the store's validation record + the live page state — reported at completion; the developer directs any further review vehicle.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
