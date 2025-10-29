# Useful Commands Reference

Quick reference for common development tasks.

## 🚀 Getting Started

```bash
# Quick start with live logs (recommended)
./start-dev.sh

# Quick start with Docker logs included
./start-dev-with-docker-logs.sh

# Or manually:
docker-compose up -d        # Start MongoDB & Redis
./gradlew bootRun          # Start backend
cd frontend && npm run dev  # Start frontend
```

## 🐳 Docker Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f mongodb
docker-compose logs -f redis

# Restart services
docker-compose restart

# Remove all data (WARNING: deletes data!)
docker-compose down -v

# Check service status
docker-compose ps

# Access MongoDB shell
docker exec -it mongodb mongosh -u admin -p admin123

# Access Redis CLI
docker exec -it redis redis-cli
```

## 🔧 Backend Commands

### Development

```bash
# Run with dev profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run with prod profile
./gradlew bootRun --args='--spring.profiles.active=prod'

# Run in debug mode
./gradlew bootRun --debug-jvm

# Clean build
./gradlew clean build
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests TenantServiceTest

# Run tests with specific tag
./gradlew test --tests "*Integration*"

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Code Quality

```bash
# Format code with ktlint
./gradlew ktlintFormat

# Check code style
./gradlew ktlintCheck

# Run all quality checks
./gradlew check

# Generate reports
./gradlew build
```

### Building

```bash
# Build JAR file
./gradlew build

# Build without tests
./gradlew build -x test

# Build and run
./gradlew build && java -jar build/libs/*.jar

# Clean build
./gradlew clean build
```

## 🎨 Frontend Commands

### Development

```bash
# Install dependencies
npm install

# Start dev server
npm run dev

# Start with specific port
npm run dev -- --port 3001

# Build for production
npm run build

# Preview production build
npm run preview
```

### Code Quality

```bash
# Run linter
npm run lint

# Fix linting issues
npm run lint -- --fix

# Type check
npx tsc --noEmit

# Format code (if using Prettier)
npx prettier --write "src/**/*.{ts,tsx}"
```

### Dependencies

```bash
# Update dependencies
npm update

# Check for outdated packages
npm outdated

# Install specific package
npm install <package-name>

# Install dev dependency
npm install -D <package-name>

# Remove package
npm uninstall <package-name>

# Clean install (delete node_modules)
rm -rf node_modules && npm install
```

### Building & Deployment

```bash
# Production build
npm run build

# Analyze bundle size
npm run build -- --report

# Preview build locally
npm run preview

# Build Docker image
docker build -t landing-page-builder-frontend .
```

## 🗄️ Database Commands

### MongoDB

```bash
# Connect to MongoDB
docker exec -it mongodb mongosh -u admin -p admin123

# Switch to app database
use landing_page_builder

# List all collections
show collections

# Query tenants
db.tenants.find().pretty()

# Query pages
db.pages.find().pretty()

# Count documents
db.tenants.countDocuments()
db.pages.countDocuments()

# Create index
db.pages.createIndex({ tenantId: 1, slug: 1 })

# Drop collection (WARNING: deletes data!)
db.pages.drop()

# Export data
docker exec mongodb mongodump --db landing_page_builder --out /backup

# Import data
docker exec mongodb mongorestore /backup
```

### Redis

```bash
# Connect to Redis
docker exec -it redis redis-cli

# View all keys
KEYS *

# Get specific key
GET key_name

# Delete key
DEL key_name

# Flush all data (WARNING: deletes all data!)
FLUSHALL

# Check memory usage
INFO memory

# Monitor commands
MONITOR
```

## 📊 Monitoring Commands

### Application Health

```bash
# Check backend health
curl http://localhost:8082/actuator/health

# View application info
curl http://localhost:8082/actuator/info

# Check all actuator endpoints
curl http://localhost:8082/actuator

# View metrics
curl http://localhost:8082/actuator/metrics
```

### Logs

```bash
# View backend logs
tail -f backend.log

# View frontend logs
tail -f frontend.log

# View Docker logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f mongodb
```

## 🧪 Testing Commands

### Backend Testing

```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Run specific test
./gradlew test --tests "TenantServiceTest"

# Run tests with coverage
./gradlew test jacocoTestReport

# Run tests in continuous mode
./gradlew test --continuous
```

### Frontend Testing

```bash
# Run tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage

# Run specific test file
npm test -- SomeComponent.test.tsx
```

### API Testing

```bash
# Test signup endpoint
curl -X POST http://localhost:8082/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "subdomain": "acme",
    "tenantName": "Acme Corp"
  }'

# Test login endpoint
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# Test authenticated endpoint
curl -X GET http://localhost:8082/api/v1/pages/tenant/{tenantId} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🔍 Debugging Commands

### Backend Debugging

```bash
# Run with debug logging
./gradlew bootRun --args='--logging.level.com.example=DEBUG'

# Run with remote debugging
./gradlew bootRun --debug-jvm

# Attach debugger to port 5005
# In IntelliJ: Run > Attach to Process > 5005
```

### Frontend Debugging

```bash
# Run with source maps
npm run dev

# Build with source maps
npm run build -- --sourcemap

# Debug in browser
# Chrome DevTools: F12 > Sources
```

## 📦 Deployment Commands

### Build for Production

```bash
# Backend
./gradlew clean build -x test

# Frontend
cd frontend && npm run build

# Docker images
docker build -t landing-page-builder-backend .
docker build -t landing-page-builder-frontend ./frontend
```

### Run Production

```bash
# Backend
java -jar build/libs/landing-page-builder-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod

# Frontend (using nginx)
docker run -p 80:80 landing-page-builder-frontend
```

## 🧹 Cleanup Commands

```bash
# Clean Gradle build
./gradlew clean

# Clean npm
cd frontend && rm -rf node_modules dist

# Clean Docker
docker system prune -a

# Clean everything
./gradlew clean && \
  cd frontend && rm -rf node_modules dist && cd .. && \
  docker-compose down -v && \
  docker system prune -a
```

## 🔐 Security Commands

```bash
# Generate JWT secret
openssl rand -base64 32

# Hash password (using bcrypt)
# Use the application endpoint or a bcrypt tool

# Check security vulnerabilities (npm)
cd frontend && npm audit

# Fix vulnerabilities
npm audit fix
```

## 📈 Performance Commands

```bash
# Analyze bundle size
cd frontend && npm run build -- --report

# Profile application
./gradlew --profile build

# Check memory usage
docker stats

# Monitor system resources
htop  # or top on macOS
```

## 🆘 Troubleshooting Commands

```bash
# Check if ports are in use
lsof -i :8082  # Backend port
lsof -i :3000  # Frontend port
lsof -i :27018 # MongoDB port
lsof -i :6379  # Redis port

# Kill process on port
kill -9 $(lsof -ti:8082)

# Restart everything
docker-compose restart && \
  ./gradlew clean bootRun &
  cd frontend && npm run dev

# Check Java version
java -version

# Check Node version
node --version

# Check npm version
npm --version

# Check Docker version
docker --version
```

## 💡 Tips

- Use `./start-dev.sh` to start everything at once
- Use `docker-compose logs -f` to monitor all services
- Use browser DevTools for frontend debugging
- Use Swagger UI for API exploration
- Use IntelliJ IDEA debugger for backend debugging
- Keep logs open in separate terminals for monitoring

## 📚 More Information

- Backend README: `README.md`
- Frontend README: `frontend/README.md`
- Quick Start: `QUICKSTART.md`
- Features: `FEATURES.md`

