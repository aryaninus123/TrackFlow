#!/bin/bash

# Docker build script
echo "Building Docker images for Issue Tracker..."

# Build backend
echo ""
echo "=========================================="
echo "Building Backend Image..."
echo "=========================================="
docker build -t issue-tracker-backend:latest .

if [ $? -ne 0 ]; then
    echo "Backend build failed!"
    exit 1
fi

# Build frontend
echo ""
echo "=========================================="
echo "Building Frontend Image..."
echo "=========================================="
docker build -t issue-tracker-frontend:latest ./frontend

if [ $? -ne 0 ]; then
    echo "Frontend build failed!"
    exit 1
fi

echo ""
echo "=========================================="
echo "Docker images built successfully!"
echo "=========================================="
echo ""
echo "Images created:"
echo "  - issue-tracker-backend:latest"
echo "  - issue-tracker-frontend:latest"
echo ""
echo "To run: docker-compose up"
echo "=========================================="
