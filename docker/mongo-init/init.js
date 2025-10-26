// MongoDB initialization script for Landing Page Builder

// Switch to the application database
db = db.getSiblingDB('landing_page_builder');

// Create application user
db.createUser({
  user: 'app_user',
  pwd: 'app_password',
  roles: [
    {
      role: 'readWrite',
      db: 'landing_page_builder'
    }
  ]
});

// Create collections with initial structure and indexes
db.createCollection('tenants');
db.createCollection('pages');
db.createCollection('users');

// Create indexes for tenants
db.tenants.createIndex({ "subdomain": 1 }, { unique: true });
db.tenants.createIndex({ "createdAt": 1 });
db.tenants.createIndex({ "status": 1 });

// Create indexes for pages
db.pages.createIndex({ "tenantId": 1, "slug": 1 }, { unique: true });
db.pages.createIndex({ "tenantId": 1, "createdAt": 1 });
db.pages.createIndex({ "tenantId": 1, "published": 1 });

// Create indexes for users
db.users.createIndex({ "tenantId": 1, "email": 1 }, { unique: true });
db.users.createIndex({ "tenantId": 1 });

print('MongoDB initialization completed for Landing Page Builder');