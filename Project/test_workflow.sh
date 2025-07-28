#!/bin/bash

# Complete test workflow script
# Usage: ./test_workflow.sh

echo "🎯 Viatorello Driver API Test Workflow"
echo "======================================"
echo ""

# Check if server is running
echo "🔍 Checking if server is running..."
if curl -s http://localhost:3000/api/driver/login > /dev/null 2>&1; then
    echo "✅ Server is running on port 3000"
else
    echo "❌ Server is not running on port 3000"
    echo "💡 Please start the server with: npm start"
    exit 1
fi

echo ""

# Check if photo file exists
if [ -f "./Persona.jpg" ]; then
    echo "✅ Photo file found: ./Persona.jpg"
else
    echo "❌ Photo file not found: ./Persona.jpg"
    echo "💡 Please ensure Persona.jpg is in the current directory"
    exit 1
fi

echo ""
echo "🚀 Starting test workflow..."
echo ""

# Step 1: Register a new test user
echo "📝 Step 1: Registering new test user..."
./register_test.sh

echo ""
echo "⏱️  Waiting 2 seconds..."
sleep 2

# Step 2: Login with test1 (fallback)
echo "🔐 Step 2: Testing login with test1..."
./login_test.sh 1

echo ""
echo "✨ Test workflow completed!"
echo ""
echo "📚 Available commands:"
echo "  ./register_test.sh     - Register a new test user with auto-increment"
echo "  ./login_test.sh [num]  - Login with test[num] (default: test1)"
echo "  ./test_workflow.sh     - Run this complete workflow"
