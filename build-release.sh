#!/bin/bash

set -e
set -o pipefail

trap 'echo ERROR' ERR

cd "$(dirname "$0")"

#Ensure the project can be built before doing anything else.
./gradlew clean || exit 1
./gradlew core:build || exit 1
./gradlew desktop:dist || exit 1
./gradlew android:assembleRelease || exit 1

#Make sure we have an up-to-date git log in the text folder as "git-changelog.txt"
git log --simplify-merges --pretty=format:"%ad [%h]:%d %s" --abbrev-commit --date=short > android/assets/text/git-changelog.txt
git add android/assets/text/git-changelog.txt
git commit android/assets/text/git-changelog.txt -m "Update git changelog." || true

git add .
git commit -a -m "Autocommit for next release build." || true

version=$(head -n1 version)
version=$(($version + 1 ))
xversion="${version:0:${#version}-2}.${version: -2}"

echo "BUILD RELEASE: $xversion ($version)"

sed -i "s/version = '.*'/version = '$xversion'/g" build.gradle
sed -i "s/versionCode=\".*\"/versionCode=\"$version\"/g" android/AndroidManifest.xml
sed -i "s/versionName=\".*\"/versionName=\"$xversion\"/g" android/AndroidManifest.xml
sed -i "s/app.version=.*$/app.version=$xversion/g" ios/robovm.properties

echo "$version" > version

git add .
git commit -a -m "Bump version for release build." || true
git tag "${xversion}" || true
git push --all
git push --tags

#Build the newly tagged version.
./gradlew clean
./gradlew core:build
./gradlew desktop:dist
./gradlew android:assembleRelease

exit 0
