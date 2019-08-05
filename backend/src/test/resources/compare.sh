#!/usr/bin/env bash
# Please make sure that passed parameters (user ids) do exist in database

# delete sql output files from previous comparison
if [ -e user1Result.txt ]; then
  rm user1Result.txt
fi

if [ -e user2Result.txt ]; then
  rm user2Result.txt
fi

# $1 $2 ids of users whom tables are about to compare
userA=userA=$1
userB=userB=$2

# arguments (ids) passed from command line - assigned to variable and then passed to parametrized queries in sql files
PGPASSWORD=1234 psql -d pfm -U postgres -v "$userA" -f user1.sql -o user1Result.txt -L compare.log
PGPASSWORD=1234 psql -d pfm -U postgres -v "$userB" -f user2.sql -o user2Result.txt -L compare.log
diff user1Result.txt user2Result.txt -s
