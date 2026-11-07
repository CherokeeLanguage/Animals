# Plan #7 — Repo tooling: Rider workspace files + editor import-metadata completion

Branch: feature/5-pending-changes. Spec: .issues/7/spec.md. Deliverable is the pending diff committed verbatim.

## Items
1. **GREEN** (pre-existing change): run SC-1 instrument (grep gradle-wrapper.properties, build.gradle).
2. Stage exactly: `build.gradle`, `settings.gradle`, `gradle/`, `gradlew`, `gradlew.bat`, staged `gradle.properties` deletion, `.gitignore`, untracked `Animals.gd`; review staged diff; commit (refs #7). (The assets/**/*.import untracked set is owned by #6 per spec #7's R-3.)
3. Verify SC-2: `git status --porcelain` afterwards shows only `.issues/`, `.mono/`.
4. Verify SC-3: unit commit diff review — only scope-table surfaces.

## Dependencies
Rides after commits #5/#6 on the shared branch.

## Traceability / documentation sources
Working-tree staged state vs HEAD at a33a73b; specs 5/6/7.

## Edge cases
Watch for `.issues/` (store worktree) never being staged; `.mono/` stays untracked.
