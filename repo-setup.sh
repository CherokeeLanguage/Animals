#!/bin/bash

set -e

cd "$(dirname "$0")"

export REPO="$(basename "$(pwd)")"

echo "Running: 'cd ~; git git init --bare --share '${REPO}'; exit'"
ssh git@www.cherokeelessons.com 'cd ~; git init --bare --share '${REPO}'; exit'

git remote add origin "ssh://git@www.cherokeelessons.com/home/git/${REPO}/" || true

touch .gitignore

if [ -s .gitignore ]; then
	cat > .gitignore << EOT
*.pydevproject
.metadata
.gradle
bin/
tmp/
*.tmp
*.bak
*.swp
*~.nib
local.properties
.settings/
.loadpath

# Eclipse Core
.project

# External tool builders
.externalToolBuilders/

# Locally stored "Eclipse launch configurations"
*.launch

# CDT-specific
.cproject

# JDT-specific (Eclipse Java Development Tools)
.classpath

# PDT-specific
.buildpath

# sbteclipse plugin
.target

# TeXlipse plugin
.texlipse
EOT
fi

touch README.md || true

git add 'android/src' || true
git add 'desktop/src' || true
git add 'ios/src' || true
git add 'html/src' || true
git add 'core/src' || true

git add '*.sh' || true
git add 'pom.xml' || true
git add '.gitignore' || true
git add '*.md' || true
git add '*.java' || true
git commit -a -m "initial repo setup" || true

git push -u origin master

printf "DONE: "
read a;

exit 0

#TEMPLATE FOR TOP LEVEL .gitignore

*.pydevproject
.metadata
.gradle
bin/
tmp/
*.tmp
*.bak
*.swp
*~.nib
local.properties
.settings/
.loadpath

# Eclipse Core
.project

# External tool builders
.externalToolBuilders/

# Locally stored "Eclipse launch configurations"
*.launch

# CDT-specific
.cproject

# JDT-specific (Eclipse Java Development Tools)
.classpath

# PDT-specific
.buildpath

# sbteclipse plugin
.target

# TeXlipse plugin
.texlipse
