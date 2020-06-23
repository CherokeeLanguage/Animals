#!/bin/sh

cd "$(dirname "$0")" || exit 1

find . -name '00-make-plist.sh' -print | while read s; do
	echo "Running $s"
	bash "$s"
done

echo "Building top level plist ..."

cp /dev/null 00-plist.txt
find . -print | while read f; do
	if [ -d "$f" ]; then
		continue;
	fi
	g="$(basename "$f")"
	if [ "$g" = "make-plist.sh" ]; then
		continue;
	fi
	if [ "$g" = "plist.txt" ]; then
		continue;
	fi
	if [ "$g" = "00-make-plist.sh" ]; then
		continue;
	fi
	if [ "$g" = "00-plist.txt" ]; then
		continue;
	fi
	echo "$f" >> 00-plist.txt
done 

exit 0
