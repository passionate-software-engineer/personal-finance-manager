#!/usr/bin/env bash

# Check we've got command line arguments
if [ -z "$*" ] ; then
    echo "Need to specify command to execute"
    exit 1
fi

# Start trying and retrying
max_attempts=20;
((count = $max_attempts))
while [[ $count -ne 0 ]] ; do
    echo ""
    echo "Trying to execute command, attempt $(($max_attempts - $count + 1)) / $max_attempts"
    echo ""
    $*
    rc=$?
    if [[ $rc -eq 0 ]] ; then
        ((count = 1))
    fi
    ((count = count - 1))
done

# Print a message if we failed
if [[ $rc -ne 0 ]] ; then
    echo "Could not execute command $* after $max_attempts attempts - stopping."
fi
