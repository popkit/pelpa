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

# compile first
mvn compile

# package war
mvn package -Dmaven.test.skip=ture

# remove old file
rm -rf ${CATALINA_HOME}/webapps/mapi-drai-web

# use new war files
cp -r mapi-drai-web/target/mapi-drai-web ${CATALINA_HOME}/webapps/

# stop tomcat and start it
${CATALINA_HOME}/bin/catalina.sh stop
#kill -9 `ps -ef |grep tomcat|grep -v grep|awk '{print $2}'`
${CATALINA_HOME}/bin/catalina.sh start


