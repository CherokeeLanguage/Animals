#!/bin/bash

cd "$(dirname "$0")" || exit 1

DEST="fini-ouya"
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
	-filter Cubic \
	-resize 256x256 \
	-enhance \
	-unsharp 0 \
	-trim \
        "$x" \
	"${DEST}"/"$NewFile"

    #geom=$(gm identify "${DEST}/$NewFile" | sed 's/.*PNG //' | cut -f 1 -d '+')
    #width=$(echo $geom|cut -f 1 -d 'x')
    #height=$(echo $geom|cut -f 2 -d 'x')
    #pot=1
    #while [ $pot -lt $width ]; do pot=$(($pot*2)); done
    #width=$pot
    #pot=1
    #while [ $pot -lt $height ]; do pot=$(($pot*2)); done
    #height=$pot

	#echo $width x $height : "${DEST}"/"$NewFile"

   #gm mogrify -background none -gravity center -resize ${width}x${height} -extent ${width}x${height} "${DEST}"/"$NewFile"


done

cp 00-make-plist.sh "${DEST}"/00-make-plist.sh
bash "${DEST}"/00-make-plist.sh

exit 0

    convert \
	-define PNG:color-type=2 \
        -trim \
        -background none -scale 200x200 \
        "$x" \
        "${DEST}"/"$NewFile"

#        -gravity center -extent 200x200 \
