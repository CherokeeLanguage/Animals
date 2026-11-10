# Spec #15 — Fix the instructions-screen display overflow on the hotfix/5.12 line (wrap + vertical fit) and redeploy the jar

Repo: CherokeeLanguage/Animals (single-repo scope; the itch page records stay on the paired CherokeeLessons.com#22 — no site work). Source of requirements: the developer's "add in the display fix" (2026-11-09), the display-defect scoping thread in the session record (the player-visible overflow captured in the #13 acceptance play-through and the developer's original "appear(s) to be a bad zoom?" report, resolved below as text-layout overflow, not camera zoom), and the diagnostic captures this session produced.

## Problem

The instructions screen overflows the game frame in both text modes — proven with executed captures, not inference:

- Play-through capture (720p, TV-mode text): heading fits; paragraph rows clip mid-word at the frame edges ("presente|", "nam|", "Select a|", "squar|e", "allowe|d"); the exit footer's lower glyphs are cut by the frame bottom. Same pattern at 1920×1043 (letterboxed content box, identical in-frame clipping).
- Diagnostic captures (2026-11-09, Xvfb, clean libGDX prefs store attempt + operator store reality): 1280×720 exact 16:9 and 1920×1043 both render the TV text and the identical clip pattern; the wrapping label's layout boundary measures ~110–118% of the frame width, centered — the label wraps, at a constraint wider than the stage, so full rows still clip both edges.
- The vertical stack (13+ rows at the 64px font) exceeds the 1080p frame — the footer is pushed past the bottom.
- Content trigger: the local libGDX prefs store (`~/.prefs/com.cherokeelessons.animals.CherokeeAnimals`) carries `force-television-mode=true` — operator state on this machine (no code path writes the key; verified by world-tree grep) — so desktop runs render the longer TV instructions file and the TV labels ("Press [A] or [Select] to exit" instead of "[BACK]"). The stored `zoom=100` confirms the original "bad zoom" symptom is the text overflow at TV-content length, not camera zoom — the FitViewport letterboxing itself is correct (the 1920×1043 capture letterboxes cleanly, no crop).
- The deployed itch jar (Animals#13's rebuild, md5 5771fd53…, 59,642,607 bytes) carries this defect; the itch page currently serves it.

## Scope

The Animals repo, hotfix/5.12 line: the `ScreenInstructions` layout (constrain the instructions table to the stage's viewport-derived size; choose a label font size that fits the TV file's wrapped text fully inside the 1080p frame), the content-complete rebuild per the proven #13 procedure, the re-push through the repo's itch tooling, and the display re-verification via the established Xvfb harness. Machine prefs (`~/.prefs/…force-television-mode`) are operator state on the developer's machine — reported in this spec, never modified by this work. Other screens fit their frames (verified in the play-through captures: menu, level select, training board, options, credits) — out of scope.

## Requirements

- **R-1 — Layout fix on `hotfix/5.12`.** A bounded edit in `core/src/com/cherokeelessons/animals/ScreenInstructions.java`: the instructions table fills the gameStage (viewport-derived 1920×1080 — `setFillParent(true)`, replacing the manual `setWidth/setHeight` calls), and the label/button font size fits the TV file's wrapped text wholly inside the frame (the `fontSize` constant lowered from 64 to 44, capture-verified; both instructions files' texts fit: the TV file is the longer constraint).
- **R-2 — Rebuild.** The content-complete jar rebuilds from `hotfix/5.12` per the #13 procedure (prep chain, vendored `.tools/jdk` JDK 8, the tree's Gradle 6.5 wrapper, `core:build desktop:dist`), staged at `dist/Animals-5.12.jar`.
- **R-3 — Re-push.** `itch-push.sh` executes the re-push (jar → `win-linux-mac`; the android push is the established idempotent content-identical no-op); no version claims.
- **R-4 — Live page.** The itch page lists the re-pushed jar — the same filename, displayed size consistent with the rebuilt jar's byte size.
- **R-5 — Sibling record.** CherokeeLessons.com#22's win-linux-mac row gains the fix's redeployment note (in-place revision, referencing this spec).

## Out of scope

- The `~/.prefs` `force-television-mode` operator pref — machine state, reported here, untouched.
- Layout work on any other screen (verified fitting in the play-through captures).
- Theitorial viewport redesign: the FitViewport letterboxing is correct as-is (the 1920×1043 capture letterboxes with no crop); Godot replaces this line eventually — no forward merge.
- Any master/Godot-line change; the fix commits on `hotfix/5.12` and pushes per that line's established flow (its first fix commit `36720a1` did the same; the trunk's tooling PRs are separate).

## Approach

The defect decomposes into the label's width constraint (the `instructions` Table was sized by hand against `fullZoneBox` without parent-fill semantics — its cell layout exceeded the stage by ~10–18%, so the wrapped rows ran past the frame) and the vertical budget (TV text at 64px overflows 1080). The fix: `instructions.setFillParent(true)` (the table becomes stage-sized, so the wrapped label's constraint equals the frame) plus `fontSize` 44 (TV text at ~14 rows × ~66px line-height ≈ 924 px < 1080 — capture-verified within tolerance before any commit; the desktop file fits with headroom). Then the proven rebuild and re-push procedure; fresh Xvfb captures at both geometries verify no clipped rows and an in-frame footer. The verification harness (Xvfb driver, pyautogui/Xlib capture) already exists in the session record.

Alternatives weighed: scrolling viewport for the text — rejected, changes the interaction vocabulary of a static instructions card and exceeds the bounded-fix direction; per-text-mode font selection (48 desktop / 44 tv) — rejected as needless branching, one size fits both with the same margin; fixing the prefs store instead of the layout — rejected, the layout must fit whatever text ships (the itch jar reaches machines whose prefs state is unknown); editing the instructions text files to shorter TV copy — rejected, the copy is content the classroom needs and the developer's direction is a display fix.

## Success criteria

| ID | Criterion | Class | Instrument | Traces to |
|---|---|---|---|---|
| SC-1 | The rebuilt jar's instructions screen shows no horizontally clipped row, and the exit footer fully inside the frame — under BOTH text modes with the longer TV text as the binding constraint — at 1280×720 and 1920×1043 | behavioral | Xvfb captures at both geometries under the `force-television-mode=true` store state as-is, dispatched inspection verifying zero edge-cut rows | R-1 |
| SC-2 | The rebuilt jar is content-complete: 3,560 entries = 2,274 classes + 28 natives + 1,258 data (457 challenge images incl. `ani.png`; 421 challenge audio) | structural | `unzip -l` counts + spot files | R-2 |
| SC-3 | The itch page lists the re-pushed jar: the same filename `Animals-5.12.jar`, displayed size consistent with the rebuilt jar's byte size; the android listing unchanged | behavioral | HTTP fetch of `https://cherokee-language.itch.io/cherokee-language-animals` | R-3, R-4 |
| SC-4 | `hotfix/5.12` carries the fix commit — bounded edit in `ScreenInstructions.java` only (no other source files) | structural | `git show hotfix/5.12 --stat` (one file, ~2–6 lines) | R-1 |
| SC-5 | The #22 win-linux-mac row carries the redeployment note referencing this spec | structural | read of the sibling record post-edit | R-5 |

Traceability note: the display fix is verified by the same agent-harness route the developer directed for #13's acceptance (Xvfb play-through captures) — the instructions screen is reachable by the established click path, and both geometries reproduce the developer's window reality.

## Impact

The itch page's desktop download becomes the fixed artifact; every screen renders text inside its frame in both text modes. The 5.12 line gains its second hotfix commit; the trunk is untouched — the hotfix line stays the 5.12-era maintenance vehicle. Risk low: bounded single-file edit capture-verified before commit; idempotent re-push; machine prefs untouched.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
