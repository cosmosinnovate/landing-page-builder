# Troubleshooting 403 Forbidden Error

## ✅ FIXED - Issue Resolved

### What Was Wrong
Two issues were causing the 403 errors:

1. **Missing `/error` Endpoint Whitelist**: Spring Boot's `/error` endpoint was not whitelisted in SecurityConfig, causing cascading 403 errors when the framework tried to handle error responses.

2. **React StrictMode Duplicate Requests**: React's StrictMode in development was causing duplicate HTTP requests, with the second request missing the JWT token.

### What Was Fixed
1. Added `/error` to the SecurityConfig whitelist to allow Spring Boot's error handling to work properly
2. Disabled React StrictMode to prevent duplicate requests in development

The servers have been restarted with the fixes applied.

---

## Original Issue Description
You were experiencing a **403 Forbidden** error when trying to save pages. This error occurred when the backend denied access due to insufficient permissions.

## Root Cause
The backend requires users to have one of these roles to create or update pages:
- `ROLE_OWNER`
- `ROLE_ADMIN`
- `ROLE_EDITOR`

The 403 error indicates that your JWT access token is either:
1. Missing the `role` claim
2. Has an incorrect or insufficient role (e.g., `VIEWER`)
3. Was created with an old version of the code that didn't include roles

## Changes Made

### Backend Changes

1. **Enhanced JWT Authentication Filter** (`JwtAuthenticationFilter.kt`)
   - Added detailed debug logging to show token parsing
   - Logs user ID, email, and role from JWT token
   - Shows authorities assigned to the user
   - Better error handling for token processing

2. **Custom Access Denied Handler** (`CustomAccessDeniedHandler.kt`)
   - Provides detailed error messages for 403 errors
   - Shows user's current authorities
   - Indicates required roles for the endpoint
   - Helps diagnose permission issues

3. **Enhanced Security Logging** (`application.properties`)
   - Enabled Spring Security debug logging
   - Added security package logging
   - Better visibility into authentication process

### Frontend Changes

1. **JWT Utility Functions** (`utils/jwt.ts`)
   - `decodeJWT()` - Decodes JWT tokens client-side
   - `isTokenExpired()` - Checks if token is expired
   - `debugToken()` - Logs detailed token information to console

2. **Enhanced Auth Context** (`AuthContext.tsx`)
   - Automatically debugs tokens on load
   - Checks for expired tokens
   - Logs user information
   - Better visibility into authentication state

3. **Enhanced API Error Handling** (`api.ts`)
   - Specific handler for 403 errors
   - Decodes and logs token payload
   - Checks for missing role claim
   - Suggests logging out and back in

## Solution: How to Fix

### Step 1: Check Your Current Token

1. Open your browser's Developer Console (F12)
2. Look for the token debug information that appears on page load
3. Check if the token has a `role` field
4. Verify the role is one of: `OWNER`, `ADMIN`, or `EDITOR`

### Step 2: Log Out and Log Back In

**This is the most important step!**

1. Click your logout button or clear your session
2. Log back in with your credentials
3. The new login will generate a fresh JWT token with the proper `role` claim

### Step 3: Verify the Fix

1. Navigate to create or edit a page
2. Make some changes
3. Click "Save"
4. The save should now succeed

## Debugging Tools

### Check Token in Console

Open your browser console and run:
```javascript
const token = localStorage.getItem('accessToken');
if (token) {
  const parts = token.split('.');
  const payload = JSON.parse(atob(parts[1]));
  console.log('Token Payload:', payload);
  console.log('User Role:', payload.role);
} else {
  console.log('No token found');
}
```

### Expected Token Payload
Your token should contain:
```json
{
  "sub": "user-id-here",
  "email": "your-email@example.com",
  "tenantId": "tenant-id-here",
  "role": "OWNER",  // <-- This must be present!
  "type": "access",
  "iat": 1234567890,
  "exp": 1234571490
}
```

### Check Backend Logs

When you try to save a page, check the backend logs for:
```
JWT Token parsed - userId: xxx, email: xxx, role: OWNER
Authentication set with authorities: [ROLE_OWNER]
```

If you see:
```
JWT Token missing required claims - userId: xxx, email: xxx, role: null
```
Then your token is missing the role claim and you need to log out/in.

## Additional Checks

### 1. Verify User Role in Database

If logging out/in doesn't fix the issue, check your user document in MongoDB:

```javascript
db.users.find({ email: "your-email@example.com" })
```

The document should have:
```json
{
  "role": "OWNER",  // or ADMIN or EDITOR
  "status": "ACTIVE"
}
```

### 2. Check for Multiple Sessions

Clear all auth-related items from localStorage:
```javascript
localStorage.removeItem('accessToken');
localStorage.removeItem('refreshToken');
localStorage.removeItem('user');
```

Then log in again.

### 3. Check Network Request

In the Network tab of Developer Tools:
1. Find the failed POST/PUT request to `/api/v1/pages`
2. Check the "Headers" tab
3. Verify the `Authorization` header is present: `Bearer <token>`
4. Check the "Response" tab for detailed error information

## Prevention

To prevent this issue in the future:

1. Always test auth flows after backend changes
2. Use the token debugging tools before deploying
3. Consider adding a token refresh mechanism
4. Monitor backend logs for authentication issues

## Still Having Issues?

If you've tried all the above and still get 403 errors:

1. **Check the backend logs** - Look for the detailed error message from `CustomAccessDeniedHandler`
2. **Verify endpoint permissions** - Check `PageController.kt` to see what roles are required
3. **Test with a fresh account** - Sign up with a new account to verify the auth flow works
4. **Check CORS configuration** - Ensure your frontend origin is allowed

## Contact

If the issue persists after following these steps, check:
- Backend console output for security-related errors
- Frontend console for any JavaScript errors
- Network tab for failed requests and their details

