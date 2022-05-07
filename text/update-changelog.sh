#!/bin/bash

cd "$(dirname "$0")" || exit 1

#Make sure we have an up-to-date git log in the text folder as "changelog.txt"
git log --simplify-merges --pretty=format:"%ad [%h]:%d %s" \
    --abbrev-commit --date=short > changelog.txt
    