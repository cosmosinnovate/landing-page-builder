// MongoDB script to create a sample published page
// Run with: mongosh landing_page_builder --username app_user --password app_password --port 27018 create_sample_page.js

use('landing_page_builder');

// Create a sample homepage for demo-site tenant
db.pages.insertOne({
  "tenantId": "68fe8b83ad1f681869a26e9a", // demo-site tenant ID
  "slug": "home",
  "title": "Welcome to Demo Site",
  "metaDescription": "This is a demo landing page built with our page builder system",
  "metaKeywords": "demo, landing page, kotlin, spring boot",
  "status": "PUBLISHED",
  "publishedAt": new Date(),
  "createdAt": new Date(),
  "updatedAt": new Date(),
  "content": {
    "sections": [
      {
        "id": "hero-section",
        "type": "HERO",
        "blocks": [
          {
            "id": "hero-heading",
            "type": "HEADING",
            "content": {
              "text": "Welcome to Our Amazing Demo Site",
              "level": 1
            },
            "styling": {
              "textAlign": "CENTER",
              "fontSize": "3rem",
              "fontWeight": "bold",
              "color": "#2c3e50",
              "margin": { "top": "40px", "right": "0", "bottom": "20px", "left": "0" }
            }
          },
          {
            "id": "hero-paragraph",
            "type": "PARAGRAPH",
            "content": {
              "text": "This is a demonstration of our powerful landing page builder. Built with Kotlin, Spring Boot, and MongoDB."
            },
            "styling": {
              "textAlign": "CENTER",
              "fontSize": "1.2rem",
              "color": "#7f8c8d",
              "margin": { "top": "0", "right": "0", "bottom": "30px", "left": "0" }
            }
          },
          {
            "id": "hero-button",
            "type": "BUTTON",
            "content": {
              "text": "Get Started",
              "href": "#features",
              "newTab": false
            },
            "styling": {
              "textAlign": "CENTER",
              "backgroundColor": "#3498db",
              "color": "white",
              "padding": { "top": "15px", "right": "30px", "bottom": "15px", "left": "30px" },
              "borderRadius": "8px",
              "fontSize": "1.1rem",
              "fontWeight": "bold"
            }
          }
        ],
        "settings": {
          "backgroundColor": "#ecf0f1",
          "padding": { "top": "80px", "right": "0", "bottom": "80px", "left": "0" }
        }
      },
      {
        "id": "features-section",
        "type": "FEATURES",
        "blocks": [
          {
            "id": "features-heading",
            "type": "HEADING",
            "content": {
              "text": "Key Features",
              "level": 2
            },
            "styling": {
              "textAlign": "CENTER",
              "fontSize": "2.5rem",
              "color": "#2c3e50",
              "margin": { "top": "0", "right": "0", "bottom": "40px", "left": "0" }
            }
          },
          {
            "id": "feature-list",
            "type": "LIST",
            "content": {
              "items": [
                "Kotlin coroutines for non-blocking operations",
                "MongoDB with advanced indexing",
                "Complete SEO meta tag support",
                "Responsive HTML generation",
                "Flexible content block system"
              ],
              "ordered": false
            },
            "styling": {
              "fontSize": "1.1rem",
              "margin": { "top": "0", "right": "0", "bottom": "20px", "left": "0" }
            }
          },
          {
            "id": "divider",
            "type": "DIVIDER",
            "content": {
              "color": "#bdc3c7",
              "thickness": "2px"
            }
          }
        ],
        "settings": {
          "padding": { "top": "60px", "right": "0", "bottom": "60px", "left": "0" }
        }
      }
    ],
    "designSettings": {
      "theme": "modern",
      "primaryColor": "#3498db",
      "secondaryColor": "#2c3e50",
      "fontFamily": "Arial, sans-serif",
      "containerWidth": "1200px"
    }
  },
  "seoSettings": {
    "ogTitle": "Demo Site - Amazing Landing Pages",
    "ogDescription": "Experience our powerful landing page builder with this demo site",
    "ogImage": "https://demo-site.example.com/og-image.jpg",
    "twitterCard": "summary_large_image",
    "noIndex": false,
    "noFollow": false
  },
  "_class": "com.example.landingpagebuilder.domain.model.Page"
});

print("Sample page created successfully!");
print("You can now visit: http://localhost:8082/public/sites/demo-site");