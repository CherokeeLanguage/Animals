#!/bin/bash

export ICON="../icon512.png"
export FILTER=Lanczos

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

mkdir applecons 2> /dev/null || true

gm convert -filter ${FILTER} "$ICON" -resize 72x72 applecons/Icon-72.png
gm convert -filter ${FILTER} "$ICON" -resize 144x144 applecons/Icon-72@2x.png
gm convert -filter ${FILTER} "$ICON" -resize 57x57 applecons/Icon.png
gm convert -filter ${FILTER} "$ICON" -resize 114x114 applecons/Icon@2x.png

gm convert -filter ${FILTER} "$ICON" -resize 76x76 applecons/Icon-76.png
gm convert -filter ${FILTER} "$ICON" -resize 152x152 applecons/Icon-76@2x.png

gm convert -filter ${FILTER} "$ICON" -resize 120x120 applecons/Icon-120.png
gm convert -filter ${FILTER} "$ICON" -resize 240x240 applecons/Icon-120@2x.png

gm convert -filter ${FILTER} "$ICON" -resize 152x152 applecons/Icon-152.png
gm convert -filter ${FILTER} "$ICON" -resize 304x304 applecons/Icon-152@2x.png
