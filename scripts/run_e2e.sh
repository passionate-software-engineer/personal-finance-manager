#!/usr/bin/env bash

trap 'echo "ERROR: $BASH_SOURCE:$LINENO $BASH_COMMAND" >&2' ERR

BACKEND_PORT=$(( ( RANDOM % 5000 )  + 40000 ))
# FRONTEND_PORT=$(( ( RANDOM % 5000 )  + 46000 ))

rm -f output.log
java -jar backend/build/libs/backend-1.0.jar --server.port=$BACKEND_PORT >>output.log 2>&1 &

backend_pid=$!
echo "Starting application, pid=$backend_pid, backend_port=$BACKEND_PORT, frontend_port=$FRONTEND_PORT"

tail -f output.log | while read LOG_LINE
do
   echo "${LOG_LINE}"
   [[ "${LOG_LINE}" == *"Started Application in"* ]] && pkill -P $$ tail && echo "Application started successfully"
   [[ "${LOG_LINE}" == *"APPLICATION FAILED TO START"* ]] && pkill -P $$ tail && echo "Application failed to start" && exit 500
done

echo "Starting E2E tests"

set -e
cd frontend
sed -i "s/8088/$BACKEND_PORT/g" src/environments/environment.ts
cat src/environments/environment.ts

rm -f frontend_output.log
ng serve >>frontend_output.log 2>&1 &

tail -f frontend_output.log | while read LOG_LINE
do
   echo "${LOG_LINE}"
   [[ "${LOG_LINE}" == *"Angular Live Development Server is listening on localhost:4200"* ]] && pkill -P $$ tail && echo "Frontend started successfully"
   [[ "${LOG_LINE}" == *"ERROR in"* ]] && pkill -P $$ tail && echo "Frontend failed to start" && exit 500
done

protractor e2e/protractor.conf.js

echo "Stopping application with pid=$backend_pid"
kill $backend_pid
