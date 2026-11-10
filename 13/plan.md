# Plan — Animals#13: restore the win-linux-mac desktop channel (hotfix/5.12 rebuild + itch re-push)

Spec: `.issues/13/spec.md` — validated PASS round 5 (`.issues/13/audit/validation.yaml`).
Authorization: developer 2026-11-09, "approved for PR" — carries the item through plan → implement → verify → the PR boundary; merge stays human-only.

## Approach statement

Execute the spec's proven procedure end-to-end: clean-room rebuild of the desktop jar from `hotfix/5.12` (fresh clone, not the scratch checkout — reproducibility is the point), runtime acceptance evidence, the re-push through `itch-push.sh` as committed on #11's line (both channels per run — the android push is an idempotent content-identical no-op re-push), live-page verification, and the sibling-record revision on CherokeeLessons.com#22. The Animals tracked tree changes not at all (spec Impact); the hotfix line's commit `36720a1` is already pushed and build-verified.

Build environment facts (verified this session): system JDKs are 21/25 — too new for the tree's Gradle 6.5 wrapper; the proven JDK 8 runtime is at `/tmp/opencode/jdk8` (Temurin 8u512). Prep chain: `python3` (3.13, Pillow 13 present) runs the tag's scripts directly — the scripts' conda-env shebangs are dev-machine habit, not a dependency.

## Items (execution order)

### Item 1 — SC-1: clean-room content-complete rebuild
- Deliverable: a fresh clone of `hotfix/5.12` at `/tmp/opencode/animals-13-release`; the prep chain run (`prep_pictures.py`, `prep_audio.py`, `00-make-all-plist.sh`, the verbatim git-changelog line from `build-release.sh:25`); `JAVA_HOME=/tmp/opencode/jdk8 ./gradlew core:build desktop:dist`; the jar staged at `dist/Animals-5.12.jar` (replacing the defective 2021 staged copy — gitignored local state, superseded; its bytes remain in the site repo's checkout and butler's history).
- RED: the staged `dist/Animals-5.12.jar` (published 2021 jar) carries 28 non-class/non-native entries and zero game content — verified this session; SC-1's content check fails against it.
- GREEN: the fresh rebuild's jar carries the full content set.
- Instrument: `unzip -l` profile against (3,560 entries = 2,274 classes + 28 natives + 1,258 data files; 457 `images/challenges/` incl. `ani.png`; 421 `audio/challenges/`; spot: fonts, music, plists, `text/git-changelog.txt`).

### Item 2 — SC-2: runtime acceptance evidence
- Deliverable: executed launch of the rebuilt jar, log showing challenge data loading and menu/level-select reach with no content-missing crash (the published jar's recorded failure: `Couldn't load file: images/challenges/ani.png (Internal)` at `View3x3Selector.setImage`).
- RED: the published jar's crash at first content load — recorded evidence from the developer's run of the first partial rebuild (2026-11-09, in this session's record).
- GREEN: the rebuilt jar's launch reaches past the former crash point.
- Instrument: the spec names the developer's acceptance run (their desktop — display+audio). Agent-side execution attempts a virtual-display launch (Xvfb + the launcher's `allowSoftwareMode`); its executed log is recorded as agent-side evidence, and the instrument deviation (agent-side executor for the named developer-run executor) is reported openly in the completion report rather than silently substituted — the developer's own run remains the acceptance record whenever they provide it. Push proceeds only when runtime evidence exists from an executor able to detect the content-missing failure mode.

### Item 3 — SC-3 + SC-4: re-push and live-page verification
- Deliverable: `itch.conf` (gitignored) jar line re-enabled pointing at the staged rebuilt jar; `./itch-push.sh` executes (butler pushes jar → `win-linux-mac`; APK → `android` as the idempotent no-op); page fetched and checked.
- RED: the live page lists the android file only — the jar channel is absent (verified 2026-11-09 post-removal).
- GREEN: the page lists the restored jar (filename = the rebuilt jar's, displayed size consistent with its byte size) alongside the unchanged android listing.
- Instrument: HTTP fetch of `https://cherokee-language.itch.io/cherokee-language-animals` (filename + size at displayed granularity); butler push output/receipts show auto-numbered builds only (SC-4).

### Item 4 — R-5: sibling record revision
- Deliverable: CherokeeLessons.com#22's channel-table row for `win-linux-mac` revised in place from the struck-removal note to restored state, referencing Animals#13.
- RED: the row currently reads struck with the removal note (verified).
- GREEN: the row reads restored, pointing at #13's record.
- Instrument: read `/home/muksihs/git/CherokeeLessons.com/.issues/22/spec.md` post-edit (store synced).

### Item 5 — SC-5 + PR: durable build tooling (R-6, added 2026-11-09)
- Deliverable: JDK 8 vendored at `.tools/jdk/` (untracked); `.gitignore` gains `/.tools/jdk/` on a trunk-targeting branch — commit `a407c70` on `feature/13-build-tooling`; the PR carries this spec's one tracked change and closes #13.
- RED: `git check-ignore .tools/jdk/` fails on trunk (no rule on master) — verified 2026-11-09.
- GREEN: the rule lands; the vendored runtime resolves.
- Instrument: executed `.tools/jdk/bin/java -version` (8.0.512); `git check-ignore .tools/jdk/` on the branch's checkout.

## Ordering

1 → 2 → 3 → 4 → 5. Each item's evidence gates the next (no push before runtime acceptance; no record revision before page verification; the tooling PR last, closing the issue). No item depends on a later item.

## Coverage map

- SC-1 ← item 1 · SC-2 ← item 2 · SC-3+SC-4 ← item 3 · R-5 ← item 4 · SC-5+R-6 ← item 5 (plus the PR boundary)

## PR stage

The PR is `feature/13-build-tooling` → trunk, carrying the spec's one tracked change (`.gitignore` +/.tools/jdk/) as one squashed commit; the operational deliverables (rebuild, acceptance play-through, re-push, page verification, sibling record) ride the PR body as executed evidence. It opens after the verify pass; merge stays human-only.

## Out of scope (carried from spec)

No Animals tracked-tree change; no master/Godot work; no viewport/zoom item (pending the developer's retest verdict — files separately if confirmed); no plist pinning; no CherokeeLessons.com repo file changes.

## PR stage

The PR is `feature/13-build-tooling` → trunk, carrying the spec's one tracked change (`.gitignore` +/.tools/jdk/) as one squashed commit; the operational deliverables (rebuild, acceptance play-through, re-push, page verification, sibling record) ride the PR body as executed evidence. It opens after the verify pass; merge stays human-only.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
