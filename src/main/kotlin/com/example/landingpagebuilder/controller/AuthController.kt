package com.example.landingpagebuilder.controller

import com.example.landingpagebuilder.dto.AuthResponse
import com.example.landingpagebuilder.dto.LoginRequest
import com.example.landingpagebuilder.dto.RefreshTokenRequest
import com.example.landingpagebuilder.dto.SignupRequest
import com.example.landingpagebuilder.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user and create a tenant",
        description = "Creates a new user account and associated tenant. Returns JWT tokens for authentication.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User and tenant created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request or email/subdomain already exists"),
        ],
    )
    fun signup(
        @Valid @RequestBody request: SignupRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates a user and returns JWT tokens.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful"),
            ApiResponse(responseCode = "401", description = "Invalid credentials"),
        ],
    )
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Uses a refresh token to generate a new access token.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
        ],
    )
    fun refreshToken(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.refreshToken(request)
        return ResponseEntity.ok(response)
    }
}
