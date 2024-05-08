#!/bin/bash
# ${1} : VSR
# ${2} : TICKETS
# ${3} : ITERATION
# ${4} : BACKOFF
# ${5} : RETRY

minBackoff=100
declare -a maxBackoffs=(200 300 400)
declare -a retries=(30 60 90)


for maxBackoff in "${maxBackoffs[@]}"; do
  for retry in "${retries[@]}"; do

    echo "Run OLOCK with VSR:${1} TICKETS:${2} ITERATION:${3} MINBACKOFF:${minBackoff} MAXBACKOFF:${maxBackoff} RETRY: ${retry}"

    VSR=${1}
    TICKETS=${2}
    ITERATION=${3}
    LOCKTYPE="o-lock"
    MAXBACKOFF=${maxBackoff}
    MINBACKOFF=${minBackoff}
    RETRY=${retry}

    export VSR
    export TICKETS
    export ITERATION
    export LOCKTYPE
    export MINBACKOFF
    export MAXBACKOFF
    export RETRY

    docker compose -f docker-compose.stress-k6-only.yml up

    sleep 3
    sh ./cleanup.sh
  done
done



