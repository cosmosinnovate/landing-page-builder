package com.example.landingpagebuilder.dto

import com.example.landingpagebuilder.domain.model.TenantFeature
import com.example.landingpagebuilder.domain.model.TenantStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "Request to create a new tenant")
data class CreateTenantRequest(
    @field:NotBlank(message = "Subdomain is required")
    @field:Size(min = 3, max = 63, message = "Subdomain must be between 3 and 63 characters")
    @field:Pattern(
        regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$",
        message = "Subdomain must contain only lowercase letters, numbers, and hyphens",
    )
    @Schema(
        description = "Unique subdomain for the tenant (lowercase, alphanumeric, hyphens allowed)",
        example = "acme-corp",
        required = true,
    )
    val subdomain: String,
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(
        description = "Display name of the tenant organization",
        example = "ACME Corporation",
        required = true,
    )
    val name: String,
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @Schema(
        description = "Contact email address for the tenant",
        example = "admin@acme.com",
        required = true,
    )
    val email: String,
    @Schema(description = "Optional tenant settings and customization")
    val settings: TenantSettingsDto? = null,
)

@Schema(description = "Request to update an existing tenant")
data class UpdateTenantRequest(
    @field:Size(min = 3, max = 63, message = "Subdomain must be between 3 and 63 characters")
    @field:Pattern(
        regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$",
        message = "Subdomain must contain only lowercase letters, numbers, and hyphens",
    )
    @Schema(description = "New subdomain (optional)", example = "acme-corp-new")
    val subdomain: String? = null,
    @field:Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(description = "New tenant name (optional)", example = "ACME Corporation Ltd")
    val name: String? = null,
    @field:Email(message = "Email must be valid")
    @Schema(description = "New email address (optional)", example = "contact@acme.com")
    val email: String? = null,
    @Schema(description = "Updated tenant settings (optional)")
    val settings: TenantSettingsDto? = null,
)

@Schema(description = "Tenant details response")
data class TenantResponse(
    @Schema(description = "Unique tenant ID", example = "507f1f77bcf86cd799439011")
    val id: String,
    @Schema(description = "Tenant subdomain", example = "acme-corp")
    val subdomain: String,
    @Schema(description = "Tenant name", example = "ACME Corporation")
    val name: String,
    @Schema(description = "Contact email", example = "admin@acme.com")
    val email: String,
    @Schema(description = "Current tenant status", example = "ACTIVE")
    val status: TenantStatus,
    @Schema(description = "Tenant settings and customization")
    val settings: TenantSettingsDto,
    @Schema(description = "Timestamp when tenant was created", example = "2025-10-26T10:15:30")
    val createdAt: LocalDateTime,
    @Schema(description = "Timestamp when tenant was last updated", example = "2025-10-26T14:30:00")
    val updatedAt: LocalDateTime,
)

@Schema(description = "Tenant customization settings")
data class TenantSettingsDto(
    @Schema(description = "Custom domain for white-labeling (optional)", example = "www.acme.com")
    val customDomain: String? = null,
    @Schema(description = "Logo URL (optional)", example = "https://cdn.acme.com/logo.png")
    val logoUrl: String? = null,
    @Schema(description = "Primary brand color", example = "#007bff", defaultValue = "#007bff")
    val primaryColor: String = "#007bff",
    @Schema(description = "Secondary brand color", example = "#6c757d", defaultValue = "#6c757d")
    val secondaryColor: String = "#6c757d",
    @Schema(description = "Allow custom CSS styling", example = "true", defaultValue = "true")
    val allowCustomStyling: Boolean = true,
    @Schema(description = "Maximum number of pages allowed", example = "10", defaultValue = "10")
    val maxPages: Int = 10,
    @Schema(description = "Storage limit in bytes", example = "1048576", defaultValue = "1048576")
    val storageLimit: Long = 1048576L,
    @Schema(description = "Enabled features for this tenant")
    val features: Set<TenantFeature> = setOf(TenantFeature.BASIC_EDITOR),
)

@Schema(description = "Request to update tenant status")
data class TenantStatusUpdateRequest(
    @Schema(description = "New status for the tenant", example = "SUSPENDED", required = true)
    val status: TenantStatus,
    @Schema(description = "Optional reason for status change", example = "Payment overdue")
    val reason: String? = null,
)

@Schema(description = "Subdomain availability check response")
data class SubdomainAvailabilityResponse(
    @Schema(description = "The subdomain that was checked", example = "acme-corp")
    val subdomain: String,
    @Schema(description = "Whether the subdomain is available", example = "true")
    val available: Boolean,
)

@Schema(description = "Tenant summary statistics")
data class TenantSummaryResponse(
    @Schema(description = "Total number of tenants across all statuses", example = "150")
    val totalTenants: Long,
    @Schema(description = "Number of active tenants", example = "120")
    val activeTenantsCount: Long,
    @Schema(description = "Number of suspended tenants", example = "25")
    val suspendedTenantsCount: Long,
    @Schema(description = "Number of inactive tenants", example = "5")
    val inactiveTenantsCount: Long,
)
