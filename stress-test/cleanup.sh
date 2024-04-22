#!/bin/bash

# Endpoint and resource
url="localhost:4000/api/stress-test"

# Send DELETE request
response=$(curl -s -X DELETE "$url" -w "%{http_code}")

http_status=$(echo "$response" | tail -n1)

# Check if the DELETE was successful
if [ "$http_status" -eq 200 ]; then
    echo "Delete successful."
else
    echo "Failed to delete resource. HTTP Status: $http_status"
fi