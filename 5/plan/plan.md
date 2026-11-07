# Plan #5 — Menu navigation SFX rework + audio playback tuning

Branch: feature/5-pending-changes. Spec: .issues/5/spec.md. The deliverable is the pending diff committed verbatim — this plan stages and verifies it, no code is written.

## Items
1. **GREEN** (pre-existing change, already in working tree — verification-first): run SC-2/SC-3 structural instruments (grep EffectAudio.tscn, menu.tscn, SmartAudioStream.gd, project.godot).
2. Stage exactly: `scenes/menu/menu.gd`, `scenes/menu/menu.tscn`, `scenes/EffectAudio.tscn`, `addons/smart_audio_stream/SmartAudioStream.gd`, `project.godot`; review staged diff; commit (refs #5).
3. Verify SC-4: the unit commit contains only the scope-table files.
4. SC-1 (behavioral, menu runtime run): execute if a Godot binary is available in this environment; otherwise record unexecuted with reason (no Godot runtime in environment) in the validation record.

## Dependencies
None (independent of #6/#7 commits; branch order only).

## Traceability / documentation sources
Working-tree diff vs HEAD at a33a73b; specs 5/6/7; Godot 3.3 project context; export_presets.cfg.

## Edge cases
`assets/audio-effects/*.wav` presence (EffectAudio.tscn now references .wav — confirm files exist and are tracked).
