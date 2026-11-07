# AGENTS.md — repo-specific guidance for agents

> The agent instruction floor lives at [.opencode/AGENTS.md](.opencode/floor.md)
> (loaded by the platform). This file adds only repo-specific facts the floor
> does not carry.

## Repo in one line

A Godot 3.3 game (Godot game template by crystal-bit) re-teaching the Cherokee
animal names — created as a port of the retired libGDX app of the same name.

## The two codebases in one tree

- `core/src/com/cherokeelessons/**` is the **legacy libGDX Java** game
  (~10k lines, screens/controllers/views). It is **reference material for the
  port, not active code** — no gradle module or CI builds it; do not "fix" it,
  do not read `core/bin/`.
- The active product is everything else: `project.godot` + `scenes/`,
  `autoload/game.gd`, `classes/`, `addons/`, `assets/`.

## Build / run / test reality

- Godot **3.3** (`config_version=4`, scene format 2), **GLES2 driver**
  configured with fallback — do not "modernize" syntax to Godot 4 or GLES3.
- Main scene: `res://scenes/main/main.tscn`. Autoload: `Game`
  (`autoload/game.gd`) — scene changes go through `Game.change_scene()`, using
  the template's `pre_start()`/`start()` convention.
- **There is no runnable test suite or build command in a bare checkout.** The
  only CI is CodeQL. Verification means: opening the scene in the Godot editor
  (3.3) or structural checks (grep/git). If a Godot binary is absent, say so —
  don't fake a run.
- The root `gradlew` / `build.gradle` / `settings.gradle` are **Rider IDE
  workspace stubs, not a build** (they only provide the `idea` module; the
  wrapper is unused — Godot's Android preset uses prebuilt templates,
  `use_custom_build=false`). Never invoke them to build.

## Challenge-data pipeline (the one generated-code flow)

- `classes/ChallengeData.gd` is **generated output** — header says so. Never
  hand-edit it.
- Generator: `Assets.Source/challenges/create_challenge_data.py` (Python,
  `requirements.txt` = `pillow` + `pydub`; audio steps additionally need
  ffmpeg). Re-running it **rmtree's `assets/challenges/audio` and
  `assets/challenges/images`** and regenerates them — run it only when you
  intend a full regeneration.
- Data keys are **Cherokee syllabary strings** (UTF-8). Preserve encoding;
  fixtures live in `Assets.Source/challenges/animals-game-mco.txt`.
- Git convention: `.import` metadata files are committed alongside assets;
  `rejected-challenges/` holds unused TTS takes and is dead weight, not data.

## Audio architecture

- SFX/music run through `addons/smart_audio_stream/` (`SmartAudioStream`
  accepts any `AudioStream`), the `EffectAudio`/`WrongAudio` scenes, and per
  bus layout (`default_bus_layout.tres`); SFX sources are `.wav`
  (`assets/audio-effects/`), spoken challenge audio is `.mp3`
  (`assets/challenges/audio/`, TTS takes named `en-<id>-<f|m>-<word>.mp3`).
  Menu navigation plays through the scene-local `fx` player
  (`scenes/menu/menu.tscn`). `project.godot` sets `audio/output_latency=10`
  — deliberate; keep it.

## Text content

- User-facing strings live in `text/` (changelog/credits/instructions, English
  + Cherokee) and inline in scenes; game copy is bilingual — when editing UI,
  keep the Cherokee half intact.

## Git repo facts

- Trunk: `master` (hook-protected; branch before any edit). Platform issues:
  GitHub `CherokeeLanguage/Animals`; local mirror lives in the `.issues/`
  worktree on the orphan `issues-data` branch — never stage `/.issues/` or
  `/.mono/`.
- Committed tooling noise to ignore: `espeak-ng/`, `adb-*.sh`, `notes/`,
  `Artwork.Source/` (source artwork, incl. GDX-era tools). `readme.md` is the
  template's readme; `README.md` still describes the libGDX app — both are
  stale; trust the code, and `changelog.md` too is the template's.
