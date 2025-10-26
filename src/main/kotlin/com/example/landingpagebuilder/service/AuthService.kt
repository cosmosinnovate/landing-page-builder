package com.example.landingpagebuilder.service

import com.example.landingpagebuilder.config.JwtProperties
import com.example.landingpagebuilder.domain.model.Tenant
import com.example.landingpagebuilder.domain.model.TenantStatus
import com.example.landingpagebuilder.domain.model.User
import com.example.landingpagebuilder.domain.model.UserRole
import com.example.landingpagebuilder.domain.model.UserStatus
import com.example.landingpagebuilder.dto.AuthResponse
import com.example.landingpagebuilder.dto.LoginRequest
import com.example.landingpagebuilder.dto.RefreshTokenRequest
import com.example.landingpagebuilder.dto.SignupRequest
import com.example.landingpagebuilder.dto.UserDto
import com.example.landingpagebuilder.exception.TenantAlreadyExistsException
import com.example.landingpagebuilder.repository.TenantRepository
import com.example.landingpagebuilder.repository.UserRepository
import com.example.landingpagebuilder.security.JwtTokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tenantRepository: TenantRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
    private val authenticationManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
) {
    @Transactional
    fun signup(request: SignupRequest): AuthResponse {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        // Check if subdomain already exists
        if (tenantRepository.existsBySubdomain(request.subdomain)) {
            throw TenantAlreadyExistsException("Subdomain '${request.subdomain}' is already taken")
        }

        // Create tenant
        val tenant =
            Tenant(
                subdomain = request.subdomain,
                name = request.tenantName,
                email = request.email,
                status = TenantStatus.ACTIVE,
            )
        val savedTenant = tenantRepository.save(tenant)

        // Create user
        val user =
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                firstName = request.firstName,
                lastName = request.lastName,
                tenantId = savedTenant.id!!,
                role = UserRole.OWNER,
                status = UserStatus.ACTIVE,
                emailVerified = false,
            )
        val savedUser = userRepository.save(user)

        // Generate tokens
        return generateAuthResponse(savedUser)
    }

    fun login(request: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password),
            )
        } catch (e: AuthenticationException) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val user =
            userRepository.findByEmail(request.email)
                .orElseThrow { IllegalArgumentException("User not found") }

        // Update last login
        val updatedUser = user.copy(lastLoginAt = LocalDateTime.now())
        userRepository.save(updatedUser)

        return generateAuthResponse(updatedUser)
    }

    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val token = request.refreshToken

        if (!jwtTokenService.validateToken(token)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        if (jwtTokenService.isTokenExpired(token)) {
            throw IllegalArgumentException("Refresh token expired")
        }

        val tokenType = jwtTokenService.getTokenType(token)
        if (tokenType != "refresh") {
            throw IllegalArgumentException("Invalid token type")
        }

        val userId =
            jwtTokenService.getUserIdFromToken(token)
                ?: throw IllegalArgumentException("Invalid token")

        val user =
            userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("User not found") }

        return generateAuthResponse(user)
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        val accessToken =
            jwtTokenService.generateAccessToken(
                userId = user.id!!,
                email = user.email,
                tenantId = user.tenantId,
                role = user.role.name,
            )

        val refreshToken =
            jwtTokenService.generateRefreshToken(
                userId = user.id,
                email = user.email,
            )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = jwtProperties.accessTokenExpiration,
            user = UserDto.fromUser(user),
        )
    }
}
