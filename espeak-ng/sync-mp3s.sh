#!/bin/bash

export LC_ALL=C

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

SRC="mp3/"
DEST="../android/assets/mp3-challenges/"

echo " - Syncing android assets folder with any changed mp3s."

rsync -a --delete-after --human-readable --progress "$SRC" "$DEST"

exit 0
