#!/bin/bash
# ${1} : VSR
# ${2} : TICKETS
# ${3} : ITERATION
# ${4} : BACKOFF
# ${5} : RETRY

declare -a backoffs=(100 200 300)
declare -a retries=(30 60 90)


for backoff in "${backoffs[@]}"; do
  for retry in "${retries[@]}"; do

    echo "Run OLOCK with VSR:${1} TICKETS:${2} ITERATION:${3} BACKOFF:${backoff} RETRY: ${retry}"

    VSR=${1}
    TICKETS=${2}
    ITERATION=${3}
    LOCKTYPE="o-lock"
    BACKOFF=${backoff}
    RETRY=${retry}

    export VSR
    export TICKETS
    export ITERATION
    export LOCKTYPE
    export BACKOFF
    export RETRY

    docker compose -f docker-compose.stress-k6-only.yml up

    sleep 3
    sh ./cleanup.sh
  done
done



