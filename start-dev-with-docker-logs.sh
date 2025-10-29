#!/bin/bash

# Landing Page Builder - Development Start Script (With Docker Logs)

echo "🚀 Starting Landing Page Builder (with Docker logs)..."
echo ""

# Cleanup function
cleanup() {
    echo ""
    echo "🛑 Shutting down services..."
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        echo "   ✓ Backend stopped"
    fi
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null
        echo "   ✓ Frontend stopped"
    fi
    if [ ! -z "$DOCKER_PID" ]; then
        kill $DOCKER_PID 2>/dev/null
    fi
    docker-compose down
    echo "   ✓ Docker containers stopped"
    echo ""
    echo "Goodbye! 👋"
    exit 0
}

# Trap Ctrl+C and call cleanup
trap cleanup SIGINT SIGTERM

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start Docker services
echo "📦 Starting MongoDB and Redis..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 5

# Check if services are running
if docker-compose ps | grep -q "Up"; then
    echo "✅ MongoDB and Redis are running"
else
    echo "❌ Failed to start services"
    exit 1
fi

# Start following docker logs in background
docker-compose logs -f > docker.log 2>&1 &
DOCKER_PID=$!

echo ""
echo "🔧 Starting Backend..."
echo "   Backend will be available at: http://localhost:8082"
echo "   Swagger UI: http://localhost:8082/swagger-ui.html"
echo ""

# Start backend in background and log to file
./gradlew bootRun > backend.log 2>&1 &
BACKEND_PID=$!
echo "   Backend PID: $BACKEND_PID"

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8082/actuator/health > /dev/null 2>&1 || \
       curl -s http://localhost:8082 > /dev/null 2>&1; then
        echo "✅ Backend is running"
        break
    fi
    sleep 2
    if [ $i -eq 30 ]; then
        echo "⚠️  Backend might not be ready yet, continuing anyway..."
    fi
done

echo ""
echo "🎨 Starting Frontend..."
echo "   Frontend will be available at: http://localhost:3000"
echo ""

# Start frontend
cd frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install
fi

# Start frontend in background and log to file
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "   Frontend PID: $FRONTEND_PID"

cd ..

echo ""
echo "✅ All services started successfully!"
echo ""
echo "📊 Service Status:"
echo "   - Backend:  http://localhost:8082"
echo "   - Frontend: http://localhost:3000"
echo "   - Swagger:  http://localhost:8082/swagger-ui.html"
echo "   - MongoDB:  localhost:27018"
echo "   - Redis:    localhost:6379"
echo ""
echo "📋 Showing live logs from all services (Press Ctrl+C to stop)..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Follow all logs simultaneously including Docker
tail -f docker.log backend.log frontend.log

