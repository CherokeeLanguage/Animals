# Spec #5 — Menu navigation SFX rework + audio playback tuning

Repo: CherokeeLanguage/Animals (single-repo scope). Branch carrying the work: `port`.
Source of requirements: developer directive "reflect all pending (uncommitted) work so it can be properly committed and merged" + the working-tree diff itself (extracted, not invented).

## Problem

Finished but uncommitted audio work on `port` has no governing record: menu navigation feedback was moved from the shared `EffectAudio` singleton play path to a per-scene `fx` `AudioStreamPlayer`, the sound-effect sources in `EffectAudio.tscn` were switched MP3→WAV, `SmartAudioStream` was generalized to any `AudioStream` stream type, and engine audio latency was tightened in `project.godot`. Without a spec this cannot be committed as reviewed, traceable work.

## Scope

| Surface | Pending change |
|---|---|
| `scenes/menu/menu.gd` | plays `$fx` at ready; on selection change restarts SFX (`$fx.stop(); $fx.play()`); legacy `EffectAudio.play_audio(...)` calls commented out |
| `scenes/menu/menu.tscn` | adds `fx` `AudioStreamPlayer` node, stream `box_moved.mp3` (ExtResource id 15); button `margin_left`/`margin_right` tweaks on Instructions/Options/About/ExitButton |
| `scenes/EffectAudio.tscn` | ext_resources switched to `assets/audio-effects/*.wav` (box_moved, cash_out, ding, ding-ding-ding) |
| `addons/smart_audio_stream/SmartAudioStream.gd` | `samples` export retyped `export(Array, AudioStreamMP3)` → `export(Array, AudioStream)`; commented-out set_loop sample block retained |
| `project.godot` | `[audio] output_latency=10`; `wav` import defaults block added; `vram_compression/import_etc=true` (was etc2=false) |

## Requirements

- R-1: The pending edit set in the scope table is committed as designed — no expansion; the commented-out `EffectAudio` call sites are committed as they stand (dead-code removal is out of scope).
- R-2: Menu scene audio behavior — SFX plays once at menu-ready; a selection change restarts the SFX from its beginning (stop→play), so back-to-back navigation replays rather than overlaps.
- R-3: `EffectAudio`'s catalog sources are the `.wav` variants; the menu `fx` player's source remains `box_moved.mp3` (as edited — the format discrepancy between the two scenes is preserved; no additional conversion work).

## Success criteria

| ID | Criterion | Class | Instrument |
|---|---|---|---|
| SC-1 | Menu ready plays the nav SFX once; each up/down selection change restarts it rather than layering a second stream | behavioral | executed run of the menu scene (Godot harness) observing `$fx` playback state: play at ready, then stop→play per selection change |
| SC-2 | `EffectAudio.tscn` references the four `assets/audio-effects/*.wav` streams; `menu.tscn`'s `fx` node streams `box_moved.mp3`; `SmartAudioStream` export line reads `export(Array, AudioStream) var samples` | structural | grep the three files for the exact references |
| SC-3 | `project.godot` contains `output_latency=10` under `[audio]` and the `wav` import defaults block; `vram_compression/import_etc=true` | structural | grep project.godot |
| SC-4 | Committed tree contains the full scope table's edit set and nothing beyond it from this unit | structural | `git diff` review of the unit's commit(s) |

## Out of scope

- Removing the dead/commented-out code in `menu.gd`, `menu.tscn` annotations, `SmartAudioStream.gd`.
- Gameplay port (challenge/gameplay scenes).
- Any further audio-format migration beyond the edits listed.
- The gradle/Rider build-tooling, `.gitignore`, and untracked `Animals.gd` surfaces elsewhere in the working tree — separate unit, disposition pending.

Byline: 🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
