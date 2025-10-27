# Landing Page Builder - Frontend

A modern React frontend for the multi-tenant landing page builder, built with Vite, TypeScript, and Tailwind CSS.

## Features

- 🔐 **Authentication** - Secure signup and login with JWT tokens
- 📊 **Dashboard** - Manage all your landing pages in one place
- 🎨 **Page Builder** - Drag-and-drop interface for building landing pages
- 🧩 **Component Blocks** - Headings, paragraphs, images, buttons, lists, and more
- 📱 **Responsive Design** - Mobile-first, works on all devices
- 🚀 **Fast & Modern** - Built with Vite for instant HMR and optimized builds
- 🎯 **Type-Safe** - Full TypeScript support throughout

## Tech Stack

- **React 18** - Latest React with hooks
- **TypeScript** - Type-safe development
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **@dnd-kit** - Modern drag-and-drop library
- **Axios** - HTTP client with interceptors
- **React Hot Toast** - Beautiful notifications

## Getting Started

### Prerequisites

- Node.js 18+ and npm
- Backend API running on `http://localhost:8082`

### Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The app will be available at `http://localhost:3000`

### Building for Production

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable components
│   │   ├── BlockComponent.tsx
│   │   ├── BlockPalette.tsx
│   │   ├── PageBuilder.tsx
│   │   ├── ProtectedRoute.tsx
│   │   └── SectionComponent.tsx
│   ├── contexts/            # React contexts
│   │   └── AuthContext.tsx
│   ├── hooks/               # Custom hooks
│   ├── pages/               # Page components
│   │   ├── Dashboard.tsx
│   │   ├── Login.tsx
│   │   ├── PageEditor.tsx
│   │   └── Signup.tsx
│   ├── services/            # API services
│   │   └── api.ts
│   ├── types/               # TypeScript types
│   │   └── index.ts
│   ├── utils/               # Utility functions
│   │   └── helpers.ts
│   ├── App.tsx              # Main app component
│   ├── main.tsx             # App entry point
│   └── index.css            # Global styles
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

## Key Features

### Authentication

- JWT-based authentication with automatic token refresh
- Protected routes that redirect to login
- Persistent auth state across page reloads

### Dashboard

- View all pages with status indicators (Draft, Published, Archived)
- Filter pages by status
- Quick actions: Edit, Publish/Unpublish, Delete
- Create new pages with one click

### Page Builder

- **Drag-and-Drop Sections** - Organize your page structure
- **Block Components** - Add content blocks to sections
- **Live Editing** - Edit content inline
- **Section Types** - Header, Hero, Content, Features, Testimonials, CTA, Footer
- **Block Types** - Heading, Paragraph, Image, Button, List, Spacer, Divider

### Page Management

- Create, read, update, and delete pages
- Publish/unpublish pages
- Set page metadata (title, slug, description, keywords)
- SEO settings for each page
- Real-time autosave

## API Integration

The frontend integrates with the backend API at `/api/v1`:

- `/auth/signup` - User registration
- `/auth/login` - User authentication
- `/auth/refresh` - Token refresh
- `/tenants/*` - Tenant management
- `/pages/*` - Page CRUD operations

The API client automatically:
- Adds JWT tokens to requests
- Refreshes expired tokens
- Handles errors gracefully
- Redirects to login on authentication failure

## Styling

The app uses Tailwind CSS with a custom configuration:

- **Primary Color**: Blue (customizable)
- **Custom Animations**: fade-in, slide-up, slide-down
- **Responsive Breakpoints**: sm, md, lg, xl, 2xl
- **Utility Classes**: btn, input, card, label

## Development

### Available Scripts

```bash
npm run dev        # Start dev server
npm run build      # Build for production
npm run preview    # Preview production build
npm run lint       # Run ESLint
```

### Code Style

- ESLint with TypeScript and React plugins
- Prettier formatting (recommended)
- React Hooks best practices

## Environment Variables

The frontend proxies API requests to the backend. Configure in `vite.config.ts`:

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

## Deployment

### Docker (Recommended)

```bash
# Build production image
docker build -t landing-page-builder-frontend .

# Run container
docker run -p 3000:80 landing-page-builder-frontend
```

### Static Hosting

The build output in `dist/` can be served by any static hosting service:

- Netlify
- Vercel
- AWS S3 + CloudFront
- GitHub Pages

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

MIT License - see LICENSE file for details

