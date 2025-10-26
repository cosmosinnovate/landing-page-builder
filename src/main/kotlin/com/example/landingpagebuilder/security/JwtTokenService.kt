package com.example.landingpagebuilder.security

import com.example.landingpagebuilder.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtTokenService(
    private val jwtProperties: JwtProperties,
) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(
        userId: String,
        email: String,
        tenantId: String,
        role: String,
    ): String =
        Jwts
            .builder()
            .subject(userId)
            .claim("email", email)
            .claim("tenantId", tenantId)
            .claim("role", role)
            .claim("type", "access")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
            .signWith(secretKey)
            .compact()

    fun generateRefreshToken(
        userId: String,
        email: String,
    ): String =
        Jwts
            .builder()
            .subject(userId)
            .claim("email", email)
            .claim("type", "refresh")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration))
            .signWith(secretKey)
            .compact()

    fun validateToken(token: String): Boolean =
        try {
            parseClaims(token)
            true
        } catch (e: Exception) {
            false
        }

    fun getUserIdFromToken(token: String): String? =
        try {
            parseClaims(token).subject
        } catch (e: Exception) {
            null
        }

    fun getEmailFromToken(token: String): String? =
        try {
            parseClaims(token).get("email", String::class.java)
        } catch (e: Exception) {
            null
        }

    fun getTenantIdFromToken(token: String): String? =
        try {
            parseClaims(token).get("tenantId", String::class.java)
        } catch (e: Exception) {
            null
        }

    fun getRoleFromToken(token: String): String? =
        try {
            parseClaims(token).get("role", String::class.java)
        } catch (e: Exception) {
            null
        }

    fun getTokenType(token: String): String? =
        try {
            parseClaims(token).get("type", String::class.java)
        } catch (e: Exception) {
            null
        }

    fun isTokenExpired(token: String): Boolean =
        try {
            parseClaims(token).expiration.before(Date())
        } catch (e: Exception) {
            true
        }

    private fun parseClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
