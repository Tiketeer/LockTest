#!/bin/bash
# ${1} : VSR
# ${2} : TICKETS
# ${3} : ITERATION
# ${4} : WAITTIME
# ${5} : LEASETIME

declare -a waittimes=(100 300)
declare -a leasetimes=(50 100)

for waittime in "${waittimes[@]}"; do
  for leasetime in "${leasetimes[@]}"; do

    echo "Run with VSR:${1} TICKETS:${2} ITERATION:${3} WAITTIME:${waittime} LEASETIME: ${leasetime}"

    VSR=${1}
    TICKETS=${2}
    ITERATION=${3}
    LOCKTYPE="d-lock"
    WAITTIME=${waittime}
    LEASETIME=${leasetime}

    export VSR
    export TICKETS
    export ITERATION
    export LOCKTYPE
    export WAITTIME
    export LEASETIME

    docker compose -f docker-compose.stress-k6-only.yml up

    sleep 1
    sh ./cleanup.sh
  done
done
