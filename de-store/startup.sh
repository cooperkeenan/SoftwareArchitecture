#!/bin/bash

echo "🚀 Starting DE-Store Services..."

# Get the absolute path to the project directory
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Function to open a new terminal and run a command
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    echo "Starting $service_name on port $port..."
    
    osascript <<EOF
tell application "Terminal"
    do script "cd '$PROJECT_DIR/$service_dir' && echo '🟢 Starting $service_name on port $port...' && mvn spring-boot:run"
    set custom title of front window to "$service_name - Port $port"
end tell
EOF
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "⚠️  Docker is not running. Please start Docker Desktop first."
    exit 1
fi

# Start RabbitMQ if not already running
echo "🐰 Starting RabbitMQ..."
cd "$PROJECT_DIR"
docker compose up -d rabbitmq

# Wait a bit for RabbitMQ to be ready
echo "⏳ Waiting for RabbitMQ to be ready..."
sleep 5

# Start all services
start_service "Price Service" "de-store-price-service" "8081"
sleep 3

start_service "Inventory Service" "de-store-inventory-service" "8082"
sleep 3

start_service "Finance Gateway" "de-store-finance-gateway" "8083"
sleep 3

start_service "Notification Service" "de-store-notification-service" "8084"
sleep 3

start_service "Loyalty Service" "de-store-loyalty-service" "8085"

echo ""
echo "✅ All services starting!"
echo ""
echo "📋 Service URLs:"
echo "   Price Service:        http://localhost:8081/swagger-ui.html"
echo "   Inventory Service:    http://localhost:8082/swagger-ui.html"
echo "   Finance Gateway:      http://localhost:8083/swagger-ui.html"
echo "   Notification Service: http://localhost:8084"
echo "   Loyalty Service:      http://localhost:8085/swagger-ui.html"
echo "   RabbitMQ Management:  http://localhost:15672 (destore/destore123)"
echo ""
echo "💡 Watch the Notification Service terminal for alerts!"