#!/bin/bash

# Script to login with test1 user (or any specified test user)
# Usage: ./login_test.sh [test_number]
# Example: ./login_test.sh 1  (or just ./login_test.sh for test1)

# Base URL for the API
BASE_URL="http://localhost:3000/api/driver"

# Get test number from argument or default to 1
TEST_NUM=${1:-1}

echo "🔐 Logging in as test$TEST_NUM"
echo "📧 Email: test$TEST_NUM@example.com"
echo "🔑 Password: Testing1"
echo ""

# Perform login
response=$(curl -w "\n%{http_code}" -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "test'$TEST_NUM'@example.com",
        "password": "Testing1"
    }')

# Extract HTTP status code
http_code=$(echo "$response" | tail -n1)
json_response=$(echo "$response" | head -n -1)

echo "📋 Login Response:"
echo "$json_response" | jq . 2>/dev/null || echo "$json_response"
echo ""

if [ "$http_code" -eq 200 ]; then
    echo "✅ Login successful!"
    
    # Extract token from response
    token=$(echo "$json_response" | jq -r '.token' 2>/dev/null)
    
    if [ "$token" != "null" ] && [ -n "$token" ]; then
        echo "🎟️  Token: $token"
        echo ""
        echo "💡 You can now use this token for authenticated requests:"
        echo "   curl -H \"Authorization: Bearer $token\" $BASE_URL/profile"
        echo ""
        
        # Save token to file for other scripts
        echo "$token" > /tmp/viatorello_token.txt
        echo "💾 Token saved to /tmp/viatorello_token.txt"
        
        # Offer to get profile
        echo ""
        read -p "🤔 Do you want to get the profile now? (y/n): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "👤 Getting profile..."
            profile_response=$(curl -s -H "Authorization: Bearer $token" "$BASE_URL/profile")
            echo "$profile_response" | jq . 2>/dev/null || echo "$profile_response"
            
            # Check if user has photo
            has_photo=$(echo "$profile_response" | jq -r '.hasPhoto' 2>/dev/null)
            if [ "$has_photo" = "true" ]; then
                echo ""
                read -p "📸 User has a photo. Download it? (y/n): " -n 1 -r
                echo ""
                if [[ $REPLY =~ ^[Yy]$ ]]; then
                    echo "📥 Downloading photo..."
                    curl -H "Authorization: Bearer $token" "$BASE_URL/photo" -o "test${TEST_NUM}_photo.jpg"
                    echo "✅ Photo saved as test${TEST_NUM}_photo.jpg"
                fi
            fi
        fi
    fi
else
    echo "❌ Login failed with HTTP code: $http_code"
    if echo "$json_response" | grep -i "not found\|invalid" > /dev/null; then
        echo "💡 User might not exist. Try running ./register_test.sh first"
    fi
fi
