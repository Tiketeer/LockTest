#!/bin/bash

declare -a vsrs=(30 60)
declare -a tickets=(30 60)

if [[ -z "$1" ]] || ! [[ "$1" =~ ^[0-9]+$ ]]; then
    echo "Usage: $0 <number of iterations>"
    exit 1
fi

for vsr in "${vsrs[@]}"; do
  for ticket in "${tickets[@]}"; do
    for ((i = 1; i <= $1; i++)); do
      # sh ./run_plock.sh ${vsr} ${ticket} ${i}
      sh ./run_olock.sh ${vsr} ${ticket} ${i}
    done
  done
done