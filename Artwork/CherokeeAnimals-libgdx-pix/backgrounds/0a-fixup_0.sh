#!/bin/bash

DEST="fini"
PIX="$(dirname "$0")"
if [ ${PIX}x = x ]; then PIX="."; fi
rm -rf "${DEST}/"
mkdir "${DEST}"
for x in "$PIX"/*[Tt][Ii][Ff] "$PIX"/*[Ss][Vv][Gg] "$PIX"/*[Jj][Pp][Gg] "$PIX"/*[Gg][Ii][Ff] "$PIX"/*[Jj][Pp][Ee][Gg] "$PIX"/*[Pp][Nn][Gg]; do
    if [ ! -f "$x" ]; then continue; fi
    
    NewFile="$(basename "$x" | sed 's/\....$//' | sed 's/animals_//')".png
    echo "$x -> $NewFile"
    
    convert \
	-background none \
	-depth 24 \
	-scale 256x256 \
	-trim \
        "$x" \
	"${DEST}"/"$NewFile"

   mogrify -background none -gravity northwest -extent 256x256 "${DEST}"/"$NewFile"

done

exit 0

    convert \
	-define PNG:color-type=2 \
        -trim \
        -background none -scale 200x200 \
        "$x" \
        "${DEST}"/"$NewFile"

#        -gravity center -extent 200x200 \
