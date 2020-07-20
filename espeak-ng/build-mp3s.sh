#!/bin/bash

export LC_ALL=C

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

vol=100

cd "$(dirname "$0")"

ff="tmp-ffmpeg.sh"
cp /dev/null "$ff"

mp3dir="./mp3"

if [ -d "$mp3dir" ]; then
    for mp3 in "$mp3dir"/*.mp3; do
        if [ -f "$mp3" ]; then rm "$mp3"; fi
    done
else
    mkdir "$mp3dir"
fi

function rebuildEspeak {
    z="$(pwd)"
    cd ~/git/espeak-ng
    make
    make install
    cd "$z"
}

function dospeak_chr {
    local txt="${1}"
    local filename="${2}"

    local mp3="$filename".mp3
    local wav="$filename".wav

    #echo "echo $txt" >> "$ff"
    echo "${HOME}/espeak-ng/bin/espeak-ng -s 200 -a $vol -v chr+f2 -w \"$wav\" \"$txt\"" >> "$ff"
    echo "normalize-audio -q \"$wav\"" >> "$ff"
    #echo "ffmpeg -y -i \"$wav\" -codec:a libmp3lame -qscale:a 3 \"$mp3\" > /dev/null 2>&1" >> "$ff"
    echo "ffmpeg -y -i \"$wav\" -codec:a libmp3lame -qscale:a 6 \"$mp3\" > /dev/null 2>&1" >> "$ff"
    echo "rm \"$wav\"" >> "$ff"
    echo >> "$ff"
}

rebuildEspeak

file="../android/assets/espeak.tsv"

echo " - generating ffmpeg script"
cat "$file" | while read line; do
    syl="$(echo "$line" | cut -f 1)"
    chr="$(echo "$line" | cut -f 2)"
    filename="$(echo "$line" | cut -f 3 | sed 's/.mp3//g')"
    if [ "$syl" = x ]; then continue; fi
    echo "$syl - $chr - $filename"
    dospeak_chr "$chr" "$mp3dir/$filename"
done

echo " - generating wavs, normalizing, and converting to mp3s"
bash "$ff"
rm "$ff"

exit 0
