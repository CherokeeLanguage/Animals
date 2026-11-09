# Spec #11 — Convert the Animals repo's deploy target to itch.io (standard itch.io deployment)

Repo: CherokeeLanguage/Animals (single-repo scope).
Source of requirements: the developer's itch.io migration directives — the deploy-target conversion, and the deploy-source decision "the existing APK/JAR need to be published, the incomplete work is not a valid deploy source" (2026-11-09); the paired-spec split across the two repos owning the migration's halves; the BoundPronouns deployment precedent.

Paired spec: **CherokeeLessons.com#22** — the itch.io page for Cherokee Language Animals and the initial deployment of the currently-published artifacts. This spec owns the Animals repo's own release-pipeline conversion; the pair splits the app's migration across the two repos that own its halves.

## Problem

The Animals repo's releases have deployed through app stores (Google Play, Amazon). The migration makes itch.io the deploy target for the repo's releases: the paired cherokeelessons.com spec establishes the itch page and deploys the currently-published artifacts from that repo's copies; this repo's own release pipeline must follow suit, using the standard itch.io deployment the BoundPronouns repo established.

The deploy source for this migration is the existing APK/JAR — the currently-published legacy-build artifacts — not the incomplete Godot port (developer direction, 2026-11-09): the repo's conversion must bind its deploy tooling to the existing artifacts and must not wait on, or source from, the unfinished port.

## Scope

The Animals repo (`CherokeeLanguage/Animals`): repo-local itch deployment tooling per the BoundPronouns pattern — including the artifact/channel mapping for the existing APK/JAR — and the push-target binding.

Deploy-source facts (verified 2026-11-09): this repo's tree and GitHub releases hold no runnable artifact — releases 5.09/5.12 carry zero assets, and the in-tree jars are tooling only (the gradle wrapper jar and font-tooling jars under `Artwork.Source/`) plus one git-ignored legacy build leftover, `core/build/libs/core-5.12.jar` (a libGDX core-module jar, not a runnable app). The existing APK/JAR exist as the site repo's copies named in the paired spec's channel table (`Animals-5.12.jar`, `Animals-5.12-release.apk` under `content/apps/animals/`). The repo's legacy libGDX build is reference material for the Godot port — no gradle module or CI builds it (the repo's own AGENTS.md states this; the root gradle files are IDE workspace stubs), and the Godot export presets define no output paths yet (`export_presets.cfg`, all presets `export_path=""`, verified 2026-11-09). The in-progress Godot port is not a valid deploy source (developer direction, 2026-11-09): no push under this spec sources from it.

## Requirements

- **R-1 — Deployment tooling.** Repo-local `itch-push.sh` plus `itch.conf.example` mirroring the BoundPronouns pattern (`butler` resolution PATH→vendored `.tools/butler/`, `REPLACE_ME` config guard, per-channel push structure; no secrets in the repo — butler credentials live in butler's own store), with `itch.conf` gitignored.
- **R-2 — Artifact/channel mapping bound to the existing APK/JAR.** The script's artifact/channel section is written for the existing APK/JAR deploy source: channels `win-linux-mac` and `android` on the paired itch project, matching the paired spec's channel table. Artifact source paths are operator config (`itch.conf` REPLACE_ME placeholders), not repo content — this repo holds no artifact copies (deploy-source facts above).
- **R-3 — Push target binding.** The push target binds to the paired itch project `cherokee-language/cherokee-language-animals`.
- **R-4 — No version claims.** Pushes carry no `--userversion`; butler's auto-numbering stands (metadata is not trusted — no version label is verified or asserted).

## Success criteria

| ID | Criterion | Class | Instrument | Traces to |
|---|---|---|---|---|
| SC-1 | `itch-push.sh` + `itch.conf.example` exist carrying the BoundPronouns-pattern guards (REPLACE_ME guard, butler resolution PATH→vendored, per-channel push structure); `itch.conf` is gitignored | structural | Read the script and config; check `.gitignore` | R-1 |
| SC-2 | The artifact/channel section binds the deploy source to the existing APK/JAR: the channel set is `win-linux-mac` and `android` on the paired itch project, matching the paired spec's channel table, and artifact paths are config placeholders rather than repo content | structural | Read the script and config against the paired spec's channel table | R-2 |
| SC-3 | The script and config bind the push target `cherokee-language/cherokee-language-animals` | structural | Read the script and config | R-3 |
| SC-4 | Pushes carry no version claims | structural | Script read (`--userversion` absent) | R-4 |

## Approach

Mirror the BoundPronouns repo's `itch-push.sh`/`itch.conf(.example)` pattern, adapted to this repo's state: the script carries the pattern's guard structure, the target binding, and the artifact/channel section for the existing APK/JAR; the artifact paths the operator fills into `itch.conf` point at local copies of the existing APK/JAR (the site repo's copies are the located reference set).

No production push is performed in this work: the initial deployment of the existing APK/JAR is the paired CherokeeLessons.com#22 work (its R-4 butler pushes). Butler's channel pushes are idempotent and re-pushable — this repo's tooling can re-push the same artifacts to the same channels without conflict when exercised.

Alternatives weighed: deferring the artifact/channel section to Godot-ready release work — rejected by developer direction (2026-11-09), the deploy source is the existing APK/JAR and the incomplete port is not a valid deploy source; sourcing artifacts from a repo build — rejected, no build surface exists (legacy libGDX is reference-only; the gradle roots are IDE stubs), and the existing published artifacts are the deploy source.

## Out of scope

- The paired cherokeelessons.com work (itch page creation, initial artifact deployment of the existing APK/JAR) — CherokeeLessons.com#22.
- Any Godot-port artifact/channel mapping — the in-progress Godot port is not a valid deploy source (developer direction, 2026-11-09); its future release work is a separate concern outside this spec.
- Artifact rebuilds — only the existing APK/JAR deploy; this spec builds nothing.

## Impact

Repo-local tooling files only; no runtime behavior change; the paired itch page gains no new state from this work. The repo's release pipeline gains complete deploy tooling bound to the itch target and the existing artifacts' channels, ready to exercise against the existing APK/JAR.
