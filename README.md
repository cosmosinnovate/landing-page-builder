# Multi-Tenant Landing Page Builder

A full-stack application for creating and managing landing pages in a multi-tenant environment. Built with Spring Boot (Kotlin) backend and React (TypeScript) frontend.

## 🚀 Features

### Backend (Spring Boot + Kotlin)
- 🏢 **Multi-Tenancy** - Isolated data per tenant with subdomain support
- 🔐 **JWT Authentication** - Secure token-based authentication
- 👥 **User Management** - Role-based access control (Owner, Admin, Editor, Viewer)
- 📄 **Page Management** - CRUD operations for landing pages
- 🎨 **Page Content Structure** - Sections and blocks for flexible layouts
- 📊 **MongoDB** - Document database for flexible schema
- 🚦 **Redis Caching** - Fast data access and session management
- 📚 **OpenAPI/Swagger** - Interactive API documentation
- 🧪 **Testing** - Comprehensive test suite with TestContainers

### Frontend (React + TypeScript)
- 🎨 **Page Builder** - Drag-and-drop interface with live preview
- 🧩 **Component Library** - Headings, paragraphs, images, buttons, lists, etc.
- 📱 **Responsive Design** - Mobile-first approach with Tailwind CSS
- ⚡ **Fast Development** - Vite for instant HMR and optimized builds
- 🔒 **Secure Authentication** - JWT with automatic token refresh
- 📊 **Dashboard** - Manage all pages with filtering and quick actions
- 🎯 **Type-Safe** - Full TypeScript coverage

## 📋 Prerequisites

- **JDK 17+** for backend
- **Node.js 18+** for frontend
- **MongoDB** (via Docker or local installation)
- **Redis** (via Docker or local installation)
- **Gradle** (wrapper included)

## 🏗️ Project Structure

```
landing-page-builder/
├── src/                          # Backend source code (Kotlin)
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/example/landingpagebuilder/
│   │   │       ├── config/       # Configuration classes
│   │   │       ├── controller/   # REST controllers
│   │   │       ├── domain/       # Domain models
│   │   │       ├── dto/          # Data transfer objects
│   │   │       ├── exception/    # Exception handling
│   │   │       ├── mapper/       # Entity-DTO mappers
│   │   │       ├── repository/   # MongoDB repositories
│   │   │       ├── security/     # Security components
│   │   │       ├── service/      # Business logic
│   │   │       └── LandingPageBuilderApplication.kt
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/                     # Backend tests
│
├── frontend/                     # Frontend source code (React)
│   ├── src/
│   │   ├── components/          # Reusable components
│   │   ├── contexts/            # React contexts
│   │   ├── pages/               # Page components
│   │   ├── services/            # API services
│   │   ├── types/               # TypeScript types
│   │   ├── utils/               # Utility functions
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── package.json
│   ├── vite.config.ts
│   └── README.md
│
├── docker/                       # Docker configuration
├── config/                       # Configuration files
├── build.gradle.kts             # Gradle build file
└── docker-compose.yml           # Docker Compose setup
```

## 🚀 Getting Started

### Option 1: Using Docker Compose (Recommended)

```bash
# Start MongoDB and Redis
docker-compose up -d

# Backend will start automatically if configured
# Or start it manually:
./gradlew bootRun

# Start frontend
cd frontend
npm install
npm run dev
```

### Option 2: Manual Setup

#### 1. Start MongoDB and Redis

```bash
# MongoDB
docker run -d -p 27018:27017 --name mongo \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
  mongo:7

# Redis
docker run -d -p 6379:6379 --name redis redis:7-alpine
```

#### 2. Start Backend

```bash
# Run with development profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Or build and run JAR
./gradlew build
java -jar build/libs/landing-page-builder-0.0.1-SNAPSHOT.jar
```

Backend will be available at `http://localhost:8082`

#### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at `http://localhost:3000`

## 📚 API Documentation

Once the backend is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/api-docs

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/signup` - Register new user and tenant
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh access token

#### Tenants
- `GET /api/v1/tenants/{tenantId}` - Get tenant details
- `PUT /api/v1/tenants/{tenantId}` - Update tenant
- `GET /api/v1/tenants/subdomain/{subdomain}/availability` - Check subdomain

#### Pages
- `GET /api/v1/pages/tenant/{tenantId}` - List all pages
- `POST /api/v1/pages` - Create page
- `PUT /api/v1/pages/{pageId}` - Update page
- `DELETE /api/v1/pages/{pageId}` - Delete page
- `PATCH /api/v1/pages/{pageId}/publish` - Publish page
- `PATCH /api/v1/pages/{pageId}/unpublish` - Unpublish page

#### Public Pages
- `GET /public/sites/{subdomain}` - View homepage
- `GET /public/sites/{subdomain}/{slug}` - View specific page

## 🏗️ Architecture

### Backend Architecture

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│  Controllers    │ (REST API endpoints)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Services      │ (Business logic)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Repositories   │ (Data access)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    MongoDB      │ (Database)
└─────────────────┘
```

### Frontend Architecture

```
┌──────────────────┐
│   React Router   │
└────────┬─────────┘
         │
    ┌────┴────┐
    │         │
┌───▼───┐  ┌──▼──────┐
│ Pages │  │ Context │
└───┬───┘  └────┬────┘
    │           │
    └─────┬─────┘
          ▼
    ┌──────────┐
    │Components│
    └─────┬────┘
          │
          ▼
    ┌──────────┐
    │ Services │ (API calls)
    └──────────┘
```

## 🧪 Testing

### Backend Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests TenantServiceTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Frontend Tests

```bash
cd frontend
npm test
```

## 🛠️ Development

### Backend Development

```bash
# Run with hot reload
./gradlew bootRun

# Format code with ktlint
./gradlew ktlintFormat

# Check code style
./gradlew ktlintCheck
```

### Frontend Development

```bash
cd frontend

# Start dev server
npm run dev

# Build for production
npm run build

# Lint code
npm run lint
```

## 🔒 Security

- **JWT Authentication** with access and refresh tokens
- **Password Hashing** with BCrypt
- **CORS Configuration** for secure API access
- **Role-Based Access Control** (RBAC)
- **Secure by Default** configuration

## 🚢 Deployment

### Backend Deployment

```bash
# Build production JAR
./gradlew build -x test

# Run with production profile
java -jar build/libs/landing-page-builder-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

### Frontend Deployment

```bash
cd frontend

# Build for production
npm run build

# Deploy 'dist' folder to your hosting service
```

Deployment options:
- **Backend**: Heroku, AWS Elastic Beanstalk, Google Cloud Run, Docker
- **Frontend**: Netlify, Vercel, AWS S3 + CloudFront, GitHub Pages

## 📝 Configuration

### Backend Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8082

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27018
spring.data.mongodb.database=landing_page_builder

# JWT
jwt.secret=your-secret-key
jwt.access-token-expiration=3600000
jwt.refresh-token-expiration=86400000
```

### Frontend Configuration

Edit `frontend/vite.config.ts`:

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8082',
      changeOrigin: true,
    },
  },
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- React team for the excellent library
- All open-source contributors

## 📞 Support

For support, email support@example.com or open an issue in the repository.

---

Made with ❤️ using Spring Boot, Kotlin, React, and TypeScript

