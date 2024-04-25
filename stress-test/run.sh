#!/bin/bash

declare -a vsrs=(50 100 200)
declare -a tickets=(10 20 30)

if [[ -z "$1" ]] || ! [[ "$1" =~ ^[0-9]+$ ]]; then
    echo "Usage: $0 <number of iterations>"
    exit 1
fi

for vsr in "${vsrs[@]}"; do
  for ticket in "${tickets[@]}"; do
    for ((i = 1; i <= $1; i++)); do
      ./run_plock.sh ${vsr} ${ticket} ${i}
      sleep 3
     ./run_olock.sh ${vsr} ${ticket} ${i}
      sleep 3
      ./run_dlock.sh ${vsr} ${ticket} ${i}
      sleep 3
    done
  done
done