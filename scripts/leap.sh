#!/usr/bin/env bash

cd "$(dirname "$0")"
cd ..
git pull
if [ -d "leap" ]; then
    cd leap
    git pull
    cd ..
else
    git clone https://github.com/popkit/leap.git
fi
