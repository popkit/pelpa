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

# clean first
mvn clean;
if [ -d "pelpa-web" ]; then
    cd pelpa-web;
    mvn clean;
    cd ..
fi

# install it
mvn install;

# compile first
mvn compile

# package war
mvn package -Dmaven.test.skip=ture

# remove old file
rm -rf ${CATALINA_HOME}/webapps/pelpa

# use new war files
cp -r pelpa-web/target/pelpa ${CATALINA_HOME}/webapps/

# stop tomcat and start it
${CATALINA_HOME}/bin/catalina.sh stop
kill -9 `ps -ef |grep tomcat|grep -v grep|awk '{print $2}'`
${CATALINA_HOME}/bin/catalina.sh start


