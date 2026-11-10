#!/bin/bash

# itch-push.sh — push the published Cherokee Language Animals artifacts
# (the existing APK/JAR deploy source) to itch.io via butler.
#
# This repo builds nothing: the artifact paths come from itch.conf
# (operator config), pointing at the existing published artifacts —
# e.g. the staged dist/ copies — never at a build output.
#
# Prerequisites (all one-time, developer-side):
#   1. butler installed — either on PATH, or vendored at .tools/butler/butler.
#      To vendor into the repo (gitignored), run one of:
#        curl -L https://broth.itch.zone/butler/linux-amd64/LATEST/archive/default -o butler.zip
#        mkdir -p .tools/butler && unzip butler.zip -d .tools/butler && chmod +x .tools/butler/butler && rm butler.zip
#   2. `butler login` run once (opens a browser; writes butler's own credential store).
#   3. itch.conf filled with the real itch.io target (ITCH_USER, ITCH_GAME)
#      and the artifact paths (ITCH_JAR, ITCH_APK) — see itch.conf.example.

set -euo pipefail

cd "$(dirname "$0")"

# --- Load the itch.io target and artifact paths ---------------------------
if [ ! -f itch.conf ]; then
    echo "ERROR: itch.conf not found — run: cp itch.conf.example itch.conf," >&2
    echo "  then fill ITCH_USER, ITCH_GAME, ITCH_JAR and ITCH_APK with the" >&2
    echo "  real target and the artifact paths." >&2
    exit 1
fi

# shellcheck disable=SC1091
source itch.conf

: "${ITCH_USER:?ERROR: itch.conf must define ITCH_USER}"
: "${ITCH_GAME:?ERROR: itch.conf must define ITCH_GAME}"
: "${ITCH_JAR:?ERROR: itch.conf must define ITCH_JAR}"
: "${ITCH_APK:?ERROR: itch.conf must define ITCH_APK}"

if [ "${ITCH_USER}" = "REPLACE_ME" ] || [ "${ITCH_GAME}" = "REPLACE_ME" ] \
   || [ "${ITCH_JAR}" = "REPLACE_ME" ] || [ "${ITCH_APK}" = "REPLACE_ME" ]; then
    echo "ERROR: itch.conf still carries REPLACE_ME placeholders — fill in the real itch.io target and artifact paths." >&2
    exit 1
fi

# --- Resolve butler: PATH first, then the vendored copy -------------------
if ! command -v butler >/dev/null 2>&1; then
    if [ -x .tools/butler/butler ]; then
        BUTLER=".tools/butler/butler"
    else
        echo "ERROR: butler not found on PATH and .tools/butler/butler is missing." >&2
        echo "  Vendor it (one-time):" >&2
        echo "    curl -L https://broth.itch.zone/butler/linux-amd64/LATEST/archive/default -o butler.zip" >&2
        echo "    mkdir -p .tools/butler && unzip butler.zip -d .tools/butler && chmod +x .tools/butler/butler && rm butler.zip" >&2
        echo "  Then authenticate (one-time): butler login" >&2
        exit 1
    fi
else
    BUTLER="butler"
fi

# --- Check the configured artifacts exist ---------------------------------
if [ ! -f "${ITCH_JAR}" ]; then
    echo "ERROR: ITCH_JAR does not point at an existing file: ${ITCH_JAR}" >&2
    echo "  Point it at the desktop jar in itch.conf (e.g. the staged dist/ copy)." >&2
    exit 1
fi

if [ ! -f "${ITCH_APK}" ]; then
    echo "ERROR: ITCH_APK does not point at an existing file: ${ITCH_APK}" >&2
    echo "  Point it at the release APK in itch.conf (e.g. the staged dist/ copy)." >&2
    exit 1
fi

JAR_CHANNEL="${ITCH_USER}/${ITCH_GAME}:win-linux-mac"
APK_CHANNEL="${ITCH_USER}/${ITCH_GAME}:android"

# --- Push (idempotent patch pushes; first push creates the download) ------
# No version label is passed: butler's auto-numbering stands.
echo "==> Pushing desktop (win-linux-mac): ${ITCH_JAR}"
"${BUTLER}" push "${ITCH_JAR}" "${JAR_CHANNEL}"

echo "==> Pushing android: ${ITCH_APK}"
"${BUTLER}" push "${ITCH_APK}" "${APK_CHANNEL}"

# --- Receipts -------------------------------------------------------------
echo "==> butler receipt:"
"${BUTLER}" status "${JAR_CHANNEL}"
"${BUTLER}" status "${APK_CHANNEL}"

echo "Done."
