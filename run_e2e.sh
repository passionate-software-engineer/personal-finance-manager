#!/usr/bin/env bash

# set -e
trap 'echo "ERROR: $BASH_SOURCE:$LINENO $BASH_COMMAND" >&2' ERR

rm -f output.log
java -jar backend/build/libs/backend-1.0.jar >>output.log 2>&1 &

backend_pid=$!
echo "Starting application, pid=$backend_pid"

tail -f output.log | while read LOG_LINE
do
   echo "${LOG_LINE}"
   [[ "${LOG_LINE}" == *"Started Application in"* ]] && pkill -P $$ tail && echo "Application started successfully"
   [[ "${LOG_LINE}" == *"APPLICATION FAILED TO START"* ]] && pkill -P $$ tail && echo "Application failed to start" && exit 500
done

echo "Starting E2E tests"

cd frontend
ng e2e --port 4222

echo "Stopping application with pid=$backend_pid"
kill $backend_pid
