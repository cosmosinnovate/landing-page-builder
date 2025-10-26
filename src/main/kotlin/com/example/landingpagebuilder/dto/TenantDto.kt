package com.example.landingpagebuilder.dto

import com.example.landingpagebuilder.domain.model.TenantFeature
import com.example.landingpagebuilder.domain.model.TenantStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateTenantRequest(
    @field:NotBlank(message = "Subdomain is required")
    @field:Size(min = 3, max = 63, message = "Subdomain must be between 3 and 63 characters")
    @field:Pattern(
        regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$",
        message = "Subdomain must contain only lowercase letters, numbers, and hyphens",
    )
    val subdomain: String,
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 255, message = "Name must not exceed 255 characters")
    val name: String,
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,
    val settings: TenantSettingsDto? = null,
)

data class UpdateTenantRequest(
    @field:Size(min = 3, max = 63, message = "Subdomain must be between 3 and 63 characters")
    @field:Pattern(
        regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$",
        message = "Subdomain must contain only lowercase letters, numbers, and hyphens",
    )
    val subdomain: String? = null,
    @field:Size(max = 255, message = "Name must not exceed 255 characters")
    val name: String? = null,
    @field:Email(message = "Email must be valid")
    val email: String? = null,
    val settings: TenantSettingsDto? = null,
)

data class TenantResponse(
    val id: String,
    val subdomain: String,
    val name: String,
    val email: String,
    val status: TenantStatus,
    val settings: TenantSettingsDto,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class TenantSettingsDto(
    val customDomain: String? = null,
    val logoUrl: String? = null,
    val primaryColor: String = "#007bff",
    val secondaryColor: String = "#6c757d",
    val allowCustomStyling: Boolean = true,
    val maxPages: Int = 10,
    val storageLimit: Long = 1048576L,
    val features: Set<TenantFeature> = setOf(TenantFeature.BASIC_EDITOR),
)

data class TenantStatusUpdateRequest(
    val status: TenantStatus,
    val reason: String? = null,
)

data class SubdomainAvailabilityResponse(
    val subdomain: String,
    val available: Boolean,
)

data class TenantSummaryResponse(
    val totalTenants: Long,
    val activeTenantsCount: Long,
    val suspendedTenantsCount: Long,
    val inactiveTenantsCount: Long,
)
