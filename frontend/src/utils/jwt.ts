/**
 * Utility functions for working with JWT tokens
 */

export interface JWTPayload {
  sub: string; // userId
  email: string;
  tenantId: string;
  role: string;
  type: 'access' | 'refresh';
  iat: number;
  exp: number;
}

/**
 * Decodes a JWT token without verification (client-side only for debugging)
 */
export function decodeJWT(token: string): JWTPayload | null {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded) as JWTPayload;
  } catch (error) {
    console.error('Failed to decode JWT:', error);
    return null;
  }
}

/**
 * Checks if a JWT token is expired
 */
export function isTokenExpired(token: string): boolean {
  const payload = decodeJWT(token);
  if (!payload || !payload.exp) {
    return true;
  }

  const now = Date.now() / 1000;
  return payload.exp < now;
}

/**
 * Gets the remaining time before token expiration in seconds
 */
export function getTokenTimeRemaining(token: string): number {
  const payload = decodeJWT(token);
  if (!payload || !payload.exp) {
    return 0;
  }

  const now = Date.now() / 1000;
  return Math.max(0, payload.exp - now);
}

/**
 * Logs token information to console (for debugging)
 */
export function debugToken(token: string | null, label = 'JWT Token'): void {
  if (!token) {
    console.log(`${label}: No token found`);
    return;
  }

  const payload = decodeJWT(token);
  if (!payload) {
    console.log(`${label}: Invalid token format`);
    return;
  }

  const timeRemaining = getTokenTimeRemaining(token);
  const expiryDate = new Date(payload.exp * 1000);

  console.group(`${label} Information`);
  console.log('User ID:', payload.sub);
  console.log('Email:', payload.email);
  console.log('Tenant ID:', payload.tenantId);
  console.log('Role:', payload.role);
  console.log('Type:', payload.type);
  console.log('Issued At:', new Date(payload.iat * 1000).toLocaleString());
  console.log('Expires At:', expiryDate.toLocaleString());
  console.log('Time Remaining:', `${Math.floor(timeRemaining / 60)} minutes`);
  console.log('Is Expired:', isTokenExpired(token));
  console.groupEnd();
}

