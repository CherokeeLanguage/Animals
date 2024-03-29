#!/bin/bash

cd "$(dirname "$0")" || exit 1

DEST="web"
PLIST="${DEST}/0plist.txt"
PIX="$(dirname "$0")"
if [ ${PIX}x = x ]; then PIX="."; fi
rm -rf "${DEST}/"
mkdir "${DEST}"
for x in "$PIX"/*[Tt][Ii][Ff] "$PIX"/*[Ss][Vv][Gg] "$PIX"/*[Jj][Pp][Gg] "$PIX"/*[Gg][Ii][Ff] "$PIX"/*[Jj][Pp][Ee][Gg] "$PIX"/*[Pp][Nn][Gg]; do
    if [ ! -f "$x" ]; then continue; fi
    
    NewFile="$(basename "$x" | sed 's/\....$//' | sed 's/animals_//')".png
    echo "$x -> $NewFile"

    gm convert \
	-background none \
	-depth 24 \
        -trim \
	-scale 128x128 \
	-gravity center \
	-extent 128x128 \
        "$x" \
	"${DEST}"/"$NewFile"

    echo "$NewFile" >> "$PLIST"

#    geom=$(gm identify "${DEST}/$NewFile" | sed 's/.*PNG //' | cut -f 1 -d '+')
#    width=$(echo $geom|cut -f 1 -d 'x')
#    height=$(echo $geom|cut -f 2 -d 'x')
#    pot=1
#    while [ $pot -lt $width ]; do pot=$(($pot*2)); done
#    width=$pot
#    pot=1
#    while [ $pot -lt $height ]; do pot=$(($pot*2)); done
#    height=$pot
#
#	echo $width x $height : "${DEST}"/"$NewFile"

#   gm mogrify -background none -gravity center -resize ${width}x${height} -extent ${width}x${height} "${DEST}"/"$NewFile"


done

exit 0

    convert \
	-define PNG:color-type=2 \
        -trim \
        -background none -scale 200x200 \
        "$x" \
        "${DEST}"/"$NewFile"

#        -gravity center -extent 200x200 \
