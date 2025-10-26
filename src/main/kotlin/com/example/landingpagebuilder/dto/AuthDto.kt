package com.example.landingpagebuilder.dto

import com.example.landingpagebuilder.domain.model.User
import com.example.landingpagebuilder.domain.model.UserRole
import com.example.landingpagebuilder.domain.model.UserStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "Request to register a new user and create a tenant")
data class SignupRequest(
    @Schema(description = "User's first name", example = "John", required = true)
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    @Schema(description = "User's last name", example = "Doe", required = true)
    @field:NotBlank(message = "Last name is required")
    val lastName: String,
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,
    @Schema(description = "User's password", example = "SecurePassword123!", required = true)
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    @Schema(
        description = "Unique subdomain for the tenant",
        example = "acme-corp",
        required = true,
    )
    @field:NotBlank(message = "Subdomain is required")
    val subdomain: String,
    @Schema(description = "Tenant/Company name", example = "Acme Corporation", required = true)
    @field:NotBlank(message = "Tenant name is required")
    val tenantName: String,
)

@Schema(description = "Request to login")
data class LoginRequest(
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,
    @Schema(description = "User's password", example = "SecurePassword123!", required = true)
    @field:NotBlank(message = "Password is required")
    val password: String,
)

@Schema(description = "Request to refresh access token")
data class RefreshTokenRequest(
    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
)

@Schema(description = "Authentication response with tokens and user information")
data class AuthResponse(
    @Schema(description = "Access token (short-lived)")
    val accessToken: String,
    @Schema(description = "Refresh token (long-lived)")
    val refreshToken: String,
    @Schema(description = "Token type", example = "Bearer")
    val tokenType: String = "Bearer",
    @Schema(description = "Access token expiration in milliseconds")
    val expiresIn: Long,
    @Schema(description = "User information")
    val user: UserDto,
)

@Schema(description = "User information")
data class UserDto(
    @Schema(description = "User ID")
    val id: String,
    @Schema(description = "Email address")
    val email: String,
    @Schema(description = "First name")
    val firstName: String,
    @Schema(description = "Last name")
    val lastName: String,
    @Schema(description = "Full name")
    val fullName: String,
    @Schema(description = "Tenant ID")
    val tenantId: String,
    @Schema(description = "User role")
    val role: UserRole,
    @Schema(description = "User status")
    val status: UserStatus,
    @Schema(description = "Email verified")
    val emailVerified: Boolean,
    @Schema(description = "Last login timestamp")
    val lastLoginAt: LocalDateTime?,
    @Schema(description = "Account creation timestamp")
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromUser(user: User): UserDto =
            UserDto(
                id = user.id!!,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                fullName = user.fullName,
                tenantId = user.tenantId,
                role = user.role,
                status = user.status,
                emailVerified = user.emailVerified,
                lastLoginAt = user.lastLoginAt,
                createdAt = user.createdAt,
            )
    }
}
