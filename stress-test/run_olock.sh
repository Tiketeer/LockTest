#!/bin/bash
# ${1} : VSR
# ${2} : TICKETS
# ${3} : ITERATION
# ${4} : BACKOFF
# ${5} : RETRY

echo "Run with VSR:${1} TICKETS:${2} ITERATION:${3} BACKOFF:${4} RETRY: ${5}"

VSR=${1}
TICKETS=${2}
ITERATION=${3}
LOCKTYPE="o-lock"
BACKOFF=${4}
RETRY=${5}

export VSR
export TICKETS
export ITERATION
export LOCKTYPE
export BACKOFF
export RETRY

docker compose -f docker-compose.stress-k6-only.yml up
