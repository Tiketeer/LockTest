#!/bin/bash

# Define variables

# global variables
declare -i run_count=3
declare -a vsrs=(300 600 900)
declare -a tickets=(30 60 90)

# Optimistic Lock specific
declare -a backoffs=(100 300 500)
declare -a retries=(10 50 100)

# Distributed Lock specific
declare -a waitTimes=(100 300 500)
declare -a leaseTimes=(100 300 500)


function run_k6_n_times {
  for ((i = 1; i <= $1; i++)); do
    for vsr in "${vsrs[@]}"; do
      for ticket in "${tickets[@]}"; do
        run_optimistic_lock_test ${vsr} ${ticket} ${i}
        run_distributed_lock_test ${var} ${ticket} ${i}
        run_pessimistic_lock_test ${var} ${ticket} ${i}
      done
    done
  done
}

# $1 vsr $2 ticket $3 currentRun
function run_optimistic_lock_test {
  for backoff in "${backoffs[@]}"; do
    for retry in "${retries[@]}"; do
      filename=$(create_file_name olock $1 $2 $backoff $retry 0 0 $3)
      echo ${filename}
    done
  done
}

# $1 vsr $2 ticket $3 currentRun
function run_distributed_lock_test {
  for waitTime in "${waitTimes[@]}"; do
    for leaseTime in "${leaseTimes[@]}"; do
      filename=$(create_file_name dlock $1 $2 0 0 $waitTime $leaseTime $3)
      echo ${filename}
    done
  done
}

function run_pessimistic_lock_test {
  filename=$(create_file_name plock $1 $2 0 0 0 0 $3)
  echo ${filename}
}

function create_file_name {
  echo "${1}_vus_${2}_tickets_${3}_backoff_${4}_retry_${5}_waitTime_${6}_leaseTime_${7}_${8}.csv"
}

run_k6_n_times run_count

