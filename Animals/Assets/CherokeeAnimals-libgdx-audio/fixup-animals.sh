#!/bin/bash

X=animals

cd "$(dirname "$0")" || exit 1

rm -rfv "${X}"
mkdir "${X}"

for x in "${X}".source/*ogg; do
    z="$(basename "$x")"
    oggdec -o "${X}"/"$z".wav "${x}"
    normalize "${X}"/"$z".wav
    oggenc --downmix -o "${X}"/"$z" "${X}"/"$z".wav
    rm "${X}"/"$z".wav
done
