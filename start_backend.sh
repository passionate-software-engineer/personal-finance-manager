#!/usr/bin/env bash

set -e

cd app

OLD_PROCESS_ID=$(ps -ef | grep "[b]ackend-1.0.jar" | awk '{print $2}')
if [ -z "$OLD_PROCESS_ID" ]
then
  kill $OLD_PROCESS_ID
else
  echo "Process was already stopped, no need to stop it again"
fi

chmod 500 backend-1.0.jar.new
mv --force backend-1.0.jar backend-1.0.jar.bak
mv --force backend-1.0.jar.new backend-1.0.jar
nohup java -jar backend-1.0.jar --spring.profiles.active=aws >> application.log 2>> application.log &
