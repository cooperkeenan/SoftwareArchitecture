#!/bin/bash

echo "🛑 Stopping DE-Store Services..."

# Kill all Java processes running Spring Boot
pkill -9 java

# Kill all Maven processes
pkill -9 mvn

# Find and kill processes on specific ports
kill -9 $(lsof -ti:8081) 2>/dev/null || true
kill -9 $(lsof -ti:8082) 2>/dev/null || true
kill -9 $(lsof -ti:8083) 2>/dev/null || true
kill -9 $(lsof -ti:8084) 2>/dev/null || true
kill -9 $(lsof -ti:8085) 2>/dev/null || true

# Stop Docker containers
docker compose down

echo "✅ All services stopped"
echo "💡 You may need to manually close the terminal windows"
