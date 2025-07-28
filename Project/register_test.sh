#!/bin/bash

# Script to register a new test driver with auto-incrementing number
# Usage: ./register_test.sh

# Base URL for the API
BASE_URL="http://localhost:3000/api/driver"

# Function to find next available test number without double registration
find_next_test_number() {
    local num=1
    while [ $num -le 100 ]; do
        # First try to login to see if user exists (non-destructive check)
        login_response=$(curl -s -X POST "$BASE_URL/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\": \"test$num@example.com\", \"password\": \"Testing1\"}" 2>/dev/null)
        
        # If login fails with "not found", the user doesn't exist
        if echo "$login_response" | grep -i "not found\|user not found\|invalid" > /dev/null; then
            echo $num
            return
        fi
        ((num++))
    done
    echo "1"  # fallback
}

# Get the next available test number
TEST_NUM=$(find_next_test_number)
    
echo "Registering new test driver: test$TEST_NUM"
echo "Email: test$TEST_NUM@example.com"
echo "Password: Testing1"
echo ""

# Perform the registration
response=$(curl -w "\n%{http_code}" -X POST "$BASE_URL/register" \
    -F "fullName=test$TEST_NUM" \
    -F "email=test$TEST_NUM@example.com" \
    -F "password=Testing1" \
    -F "vehicle[brand]=Toyota" \
    -F "vehicle[model]=Camry" \
    -F "vehicle[year]=2020" \
    -F "vehicle[licensePlate]=ABC$TEST_NUM" \
    -F "vehicle[capacity]=4" \
    -F "currentLocation[type]=Point" \
    -F "currentLocation[coordinates][0]=1" \
    -F "currentLocation[coordinates][1]=1" \
    -F "photo=@./Persona.jpg")

# Extract HTTP status code
http_code=$(echo "$response" | tail -n1)
json_response=$(echo "$response" | head -n -1)

echo "Registration Response:"
echo "$json_response" | jq . 2>/dev/null || echo "$json_response"
echo ""

if [ "$http_code" -eq 200 ] || [ "$http_code" -eq 201 ]; then
    echo "Success! Driver test$TEST_NUM registered successfully"
    echo "You can now login with:"
    echo "Email: test$TEST_NUM@example.com"
    echo "Password: Testing1"
else
    echo "Registration failed with HTTP code: $http_code"
fi

echo ""
echo "To login with this user, run: ./login_test.sh test$TEST_NUM"
