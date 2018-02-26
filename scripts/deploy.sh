#!/usr/bin/env bash

cd "$(dirname "$0")"
cd ..
git reset --hard
git pull
if [ -d "leap" ]; then
    cd leap
    git pull
    rm -r target
    cd ..
else
    git clone https://github.com/popkit/leap.git
fi

# clean first
mvn clean;
if [ -d "pelpa-web" ]; then
    rm -r pelpa-web/target
fi

# copy config file
cp ~/config/appcontext-mybatis.xml pelpa-web/src/main/resources/config/spring
# install it
mvn install;

# compile first
mvn compile

# package war
mvn package -Dmaven.test.skip=ture

# stop tomcat and start it
${CATALINA_HOME}/bin/catalina.sh stop
kill -9 `ps -ef |grep tomcat|grep -v grep|awk '{print $2}'`
${CATALINA_HOME}/bin/catalina.sh start


