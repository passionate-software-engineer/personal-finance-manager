#!/usr/bin/env bash

set -e

attempt_counter=1
max_attempts=5

APP_URL=$1

URL="$APP_URL:8088/actuator/health"
until $(curl --output /dev/null --silent --head --fail $URL); do
    if [ ${attempt_counter} -gt ${max_attempts} ];then
      echo "Max attempts reached, application failed to startup"
      exit 1
    fi

    printf "Waiting for application to startup, attempt: $attempt_counter / ${max_attempts}. Checking $URL\n"
    attempt_counter=$(($attempt_counter+1))
    sleep 5
done

echo 'Application started successfully, checking health status'
HEALTH_STATUS=$(curl --silent $URL | jq '.status')

if [ $HEALTH_STATUS = '"UP"' ]
then
  echo 'Application is working correctly'
else
  echo "Application is not working correctly, the health status is $HEALTH_STATUS"
  exit 1
fi
