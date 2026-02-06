#!/bin/bash

# Local development startup script
echo "Starting Issue Tracker Application..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "Creating .env file from .env.example..."
    cp .env.example .env
    echo "Please edit .env file with your configuration before proceeding."
    exit 1
fi

# Start services
echo "Starting Docker containers..."
docker-compose up -d

# Wait for services to be healthy
echo "Waiting for services to start..."
sleep 10

# Check if backend is running
echo "Checking backend health..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/health > /dev/null; then
        echo "Backend is healthy!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Backend failed to start. Check logs with: docker-compose logs backend"
        exit 1
    fi
    sleep 2
done

echo ""
echo "=========================================="
echo "Issue Tracker is now running!"
echo "=========================================="
echo ""
echo "Frontend: http://localhost"
echo "Backend API: http://localhost:8080"
echo "PostgreSQL: localhost:5432"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"
echo "=========================================="
