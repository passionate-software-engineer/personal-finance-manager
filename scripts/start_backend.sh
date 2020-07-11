#!/usr/bin/env bash

set -x
set -e

DATE=$(date '+%Y-%m-%d-%H:%M')

OLD_PROCESS_ID=$(ps -ef | grep "[b]ackend-1.0.jar" | awk '{print $2}')
if [ -n "$OLD_PROCESS_ID" ]
then
  kill $OLD_PROCESS_ID
  sleep 10
else
  echo "Process was already stopped, no need to stop it again"
fi

if [ -f backend-1.0.jar.new ]; then
  if [ -f backend-1.0.jar ]; then
    mv --force backend-1.0.jar backend-1.0.jar.$DATE
  fi

  chmod 700 backend-1.0.jar.new
  mv --force backend-1.0.jar.new backend-1.0.jar
fi

if [ -f application.log ]; then
  mv application.log application.log.$DATE
fi

java -version

nohup java -jar backend-1.0.jar --spring.profiles.active=aws --spring.datasource.password=$DATABASE_PASSWORD >> application.log 2>> application.log &

sleep 30
PROCESS_ID=$(ps -ef | grep "[b]ackend-1.0.jar" | awk '{print $2}')
if [ -z "$PROCESS_ID" ]
then
  echo "Application failed to start"
  cat application.log
  exit 1
fi
