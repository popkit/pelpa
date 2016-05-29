#!/usr/bin/env bash
#################################################
# * */2 * * * /home/popkit/pelpa/scripts/sync-melpa.sh >>/home/popkit/log/sync.txt 2>&1
#################################################
echo "------------------------------------------"
cd "$(dirname "$0")"
git pull
echo `date +%Y-%m-%d-%H:%M.%S`
cd /home/popkit/melpa
git pull
cp recipes/* /home/popkit/pelpa/recipes
cd /home/popkit/pelpa
git add -A .
git commit -am "sync melpa.org recipes @ `date +%Y-%m-%d-%H:%M.%S`"
git push
