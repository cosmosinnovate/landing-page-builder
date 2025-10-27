# Quick Start Guide

Get the Landing Page Builder up and running in 5 minutes!

## Prerequisites

- JDK 17+
- Node.js 18+
- Docker (for MongoDB and Redis)

## Step 1: Start Dependencies

```bash
# Start MongoDB and Redis using Docker Compose
docker-compose up -d
```

This will start:
- MongoDB on port 27018
- Redis on port 6379

## Step 2: Start Backend

```bash
# From project root
./gradlew bootRun
```

The backend API will be available at:
- API: http://localhost:8082
- Swagger UI: http://localhost:8082/swagger-ui.html

## Step 3: Start Frontend

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend will be available at: http://localhost:3000

## Step 4: Create Your First Account

1. Open http://localhost:3000 in your browser
2. Click "Sign up" 
3. Fill in your details:
   - First Name: John
   - Last Name: Doe
   - Email: john@example.com
   - Password: password123
   - Company Name: Acme Corp
   - Subdomain: acme (will be acme.example.com)
4. Click "Create account"

## Step 5: Create Your First Page

1. You'll be redirected to the dashboard
2. Click "Create New Page"
3. Give your page a title
4. Drag and drop components from the left sidebar
5. Edit content inline by clicking on it
6. Click "Save" when done
7. Click "Publish" to make it live

## Viewing Your Published Page

Published pages are available at:
```
http://localhost:8082/public/sites/{subdomain}/{slug}
```

For example:
```
http://localhost:8082/public/sites/acme/home
```

## Default Users and Roles

After signup, your user will have the OWNER role, giving you full access to:
- Create, edit, and delete pages
- Manage tenant settings
- Publish/unpublish pages

## Troubleshooting

### Backend won't start
- Ensure MongoDB and Redis are running: `docker-compose ps`
- Check ports 8082, 27018, and 6379 are not in use
- View logs: `docker-compose logs`

### Frontend won't start
- Ensure Node.js 18+ is installed: `node --version`
- Delete node_modules and reinstall: `rm -rf node_modules && npm install`
- Clear npm cache: `npm cache clean --force`

### Can't login
- Ensure backend is running and accessible
- Check browser console for errors
- Verify MongoDB is running and accessible

## Next Steps

- Explore the Page Builder interface
- Try different block types (headings, images, buttons, etc.)
- Customize your page design
- Set up SEO metadata
- View the API documentation at http://localhost:8082/swagger-ui.html

## Useful Commands

### Backend
```bash
# Run tests
./gradlew test

# Build JAR
./gradlew build

# Format code
./gradlew ktlintFormat
```

### Frontend
```bash
# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint
```

### Docker
```bash
# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Reset everything (WARNING: deletes data)
docker-compose down -v
```

## Support

If you encounter any issues, please:
1. Check the main README.md for detailed information
2. Review the troubleshooting section above
3. Open an issue on GitHub

Happy building! 🚀

