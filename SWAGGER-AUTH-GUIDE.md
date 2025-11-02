# Swagger UI Authentication Guide

## ✅ JWT Authentication Now Enabled in Swagger UI!

You can now test all protected APIs directly from Swagger UI with JWT authentication.

---

## 🔐 How to Authenticate

### Step 1: Get Your JWT Token

#### Option A: Sign Up (New User)
1. Open Swagger UI: http://localhost:8082/swagger-ui.html
2. Find **Authentication** section
3. Expand `POST /api/v1/auth/signup`
4. Click **"Try it out"**
5. Enter your details:
```json
{
  "email": "your-email@example.com",
  "password": "your-password",
  "firstName": "Your",
  "lastName": "Name",
  "subdomain": "your-company",
  "tenantName": "Your Company"
}
```
6. Click **Execute**
7. **Copy the `accessToken`** from the response

#### Option B: Login (Existing User)
1. Open Swagger UI: http://localhost:8082/swagger-ui.html
2. Find **Authentication** section
3. Expand `POST /api/v1/auth/login`
4. Click **"Try it out"**
5. Enter credentials:
```json
{
  "email": "john.doe@expedia.com",
  "password": "password123"
}
```
6. Click **Execute**
7. **Copy the `accessToken`** from the response

### Step 2: Authorize Swagger UI

1. **Click the 🔓 Authorize button** at the top right of Swagger UI
2. In the popup dialog, **paste your JWT token** in the "Value" field
   - ⚠️ **Important**: Enter ONLY the token (no "Bearer " prefix)
3. Click **"Authorize"**
4. Click **"Close"**

### Step 3: Test Protected Endpoints

Now you can test any protected endpoint! For example:

#### Test: Get Pages
1. Find **Page Management** section
2. Expand `GET /api/v1/pages/tenant/{tenantId}`
3. Click **"Try it out"**
4. Enter your `tenantId` (from the login/signup response)
5. Click **Execute**
6. ✅ You should see a successful 200 response!

---

## 🔍 Visual Guide

### What You'll See:

**Before Authorization:**
- 🔓 Padlock icon at top = **Open** (not authenticated)
- Protected endpoints show a padlock icon 🔒

**After Authorization:**
- 🔐 Padlock icon at top = **Closed** (authenticated)
- Your JWT token is automatically included in all requests
- Protected endpoints are now accessible

---

## 📋 Example Workflow

### Complete Test Flow:

1. **Sign Up**
   ```
   POST /api/v1/auth/signup
   → Get accessToken + tenantId
   ```

2. **Authorize**
   ```
   Click 🔓 → Paste token → Authorize
   ```

3. **Create Page**
   ```
   POST /api/v1/pages
   Body:
   {
     "tenantId": "your-tenant-id",
     "title": "My Landing Page",
     "slug": "my-page",
     "status": "DRAFT"
   }
   ```

4. **Get Pages**
   ```
   GET /api/v1/pages/tenant/{tenantId}
   ```

5. **Publish Page**
   ```
   PATCH /api/v1/pages/{pageId}/publish
   ```

---

## 🛠️ Troubleshooting

### Issue: 403 Forbidden Error

**Symptoms:**
```json
{
  "timestamp": "2025-10-29T...",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

**Solutions:**

1. **Check if you're authorized:**
   - Look at the padlock icon 🔓 at the top
   - If open, click it and enter your token

2. **Token expired?**
   - JWT tokens expire after 1 hour
   - Get a new token by logging in again

3. **Wrong token format?**
   - Don't include "Bearer " prefix
   - Just paste the token string directly

4. **Invalid token?**
   - Make sure you copied the entire token
   - Check for extra spaces or newlines

### Issue: 401 Unauthorized Error

**Solution:**
- Your token is invalid or expired
- Get a new token by logging in

### Issue: Can't find Authorize button

**Solution:**
- Look for 🔓 icon at the **top right** of Swagger UI page
- Refresh the page if you don't see it

---

## 🎯 Quick Reference

| Action | Endpoint | Auth Required |
|--------|----------|--------------|
| Sign Up | `POST /api/v1/auth/signup` | ❌ No |
| Login | `POST /api/v1/auth/login` | ❌ No |
| Get Pages | `GET /api/v1/pages/tenant/{id}` | ✅ Yes |
| Create Page | `POST /api/v1/pages` | ✅ Yes |
| Update Page | `PUT /api/v1/pages/{id}` | ✅ Yes |
| Publish Page | `PATCH /api/v1/pages/{id}/publish` | ✅ Yes |
| Delete Page | `DELETE /api/v1/pages/{id}` | ✅ Yes |

---

## 💡 Pro Tips

1. **Keep your token handy**: Copy it to a text editor while testing
2. **Use the refresh token**: When your access token expires, use the refresh endpoint
3. **Test with different roles**: Sign up multiple users to test OWNER/ADMIN/EDITOR permissions
4. **Check the response**: The accessToken is in the response body, not headers
5. **Browser DevTools**: Open Console (F12) to see detailed error messages if requests fail

---

## 📝 Token Details

### Access Token
- **Expiration**: 1 hour (3600 seconds)
- **Usage**: All authenticated API requests
- **Format**: JWT (JSON Web Token)

### Refresh Token  
- **Expiration**: 24 hours (86400 seconds)
- **Usage**: Get new access token without re-login
- **Endpoint**: `POST /api/v1/auth/refresh`

---

## 🚀 You're Ready!

Your Swagger UI now has full JWT authentication support. Test away! 🎉

**Swagger UI**: http://localhost:8082/swagger-ui.html
**API Docs**: http://localhost:8082/api-docs


