#!/bin/sh

width=1920
height=1080

for f in dsci????.png; do
	i=0
	for y in $(seq 0 270 1079); do
		for x in $(seq 0 240 1919); do
			dest="p_${i}_$f"
			echo gm convert -crop 240x270+${x}+${y} "$f" "${dest}"
			gm convert -crop 240x270+${x}+${y} "$f" "${dest}"
			i=$((${i}+1))
		done
	done
done
