#!/bin/sh

cd "$(dirname "$0")" || exit 1

cp /dev/null 00-plist.txt
ls -1 *.wav > 00-plist.txt

exit 0
