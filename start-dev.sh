#!/bin/bash

# Landing Page Builder - Development Start Script

echo "🚀 Starting Landing Page Builder..."
echo ""

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

echo ""
echo "🔧 Starting Backend..."
echo "   Backend will be available at: http://localhost:8082"
echo "   Swagger UI: http://localhost:8082/swagger-ui.html"
echo ""

# Start backend in background
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

# Start frontend in background
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "   Frontend PID: $FRONTEND_PID"

cd ..

echo ""
echo "✅ All services started successfully!"
echo ""
echo "📝 Next steps:"
echo "   1. Open http://localhost:3000 in your browser"
echo "   2. Sign up for a new account"
echo "   3. Start building your landing pages!"
echo ""
echo "📊 Service Status:"
echo "   - Backend:  http://localhost:8082"
echo "   - Frontend: http://localhost:3000"
echo "   - Swagger:  http://localhost:8082/swagger-ui.html"
echo "   - MongoDB:  localhost:27018"
echo "   - Redis:    localhost:6379"
echo ""
echo "🛑 To stop services:"
echo "   - Kill backend:  kill $BACKEND_PID"
echo "   - Kill frontend: kill $FRONTEND_PID"
echo "   - Stop Docker:   docker-compose down"
echo ""
echo "📋 Logs:"
echo "   - Backend:  tail -f backend.log"
echo "   - Frontend: tail -f frontend.log"
echo ""
echo "Happy building! 🎉"

