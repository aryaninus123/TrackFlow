#!/bin/bash

# Test execution script
echo "Running Issue Tracker Tests..."
echo ""

# Backend tests
echo "=========================================="
echo "Running Backend Tests..."
echo "=========================================="
mvn test

if [ $? -ne 0 ]; then
    echo "Backend tests failed!"
    exit 1
fi

echo ""
echo "=========================================="
echo "Running Frontend Tests..."
echo "=========================================="
cd frontend
npm test -- --watchAll=false

if [ $? -ne 0 ]; then
    echo "Frontend tests failed!"
    exit 1
fi

cd ..

echo ""
echo "=========================================="
echo "All tests passed!"
echo "=========================================="
