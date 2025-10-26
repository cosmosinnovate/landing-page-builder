package com.example.landingpagebuilder.controller

import com.example.landingpagebuilder.domain.model.TenantStatus
import com.example.landingpagebuilder.dto.CreateTenantRequest
import com.example.landingpagebuilder.dto.SubdomainAvailabilityResponse
import com.example.landingpagebuilder.dto.TenantResponse
import com.example.landingpagebuilder.dto.TenantStatusUpdateRequest
import com.example.landingpagebuilder.dto.TenantSummaryResponse
import com.example.landingpagebuilder.dto.UpdateTenantRequest
import com.example.landingpagebuilder.mapper.TenantMapper
import com.example.landingpagebuilder.service.TenantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant Management", description = "APIs for managing tenants in the multi-tenant system")
class TenantController(
    private val tenantService: TenantService,
) {
    @Operation(summary = "Create a new tenant", description = "Creates a new tenant with the provided details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Tenant created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "409", description = "Tenant with subdomain already exists"),
        ],
    )
    @PostMapping
    suspend fun createTenant(
        @Valid @RequestBody request: CreateTenantRequest,
    ): ResponseEntity<TenantResponse> {
        val tenant = TenantMapper.toEntity(request)
        val createdTenant = tenantService.createTenant(tenant)
        val response = TenantMapper.toResponse(createdTenant)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "Get tenant by ID", description = "Retrieves tenant details by their unique ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant found"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
        ],
    )
    @GetMapping("/{tenantId}")
    suspend fun getTenantById(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
    ): ResponseEntity<TenantResponse> {
        val tenant = tenantService.findById(tenantId)
        val response = TenantMapper.toResponse(tenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get tenant by subdomain", description = "Retrieves tenant details by their subdomain")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant found"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
        ],
    )
    @GetMapping("/subdomain/{subdomain}")
    suspend fun getTenantBySubdomain(
        @Parameter(description = "Tenant subdomain") @PathVariable subdomain: String,
    ): ResponseEntity<TenantResponse> {
        val tenant = tenantService.findBySubdomain(subdomain)
        val response = TenantMapper.toResponse(tenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get tenant by custom domain", description = "Retrieves tenant details by their custom domain")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant found"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
        ],
    )
    @GetMapping("/domain/{customDomain}")
    suspend fun getTenantByCustomDomain(
        @Parameter(description = "Custom domain") @PathVariable customDomain: String,
    ): ResponseEntity<TenantResponse> {
        val tenant = tenantService.findByCustomDomain(customDomain)
        val response = TenantMapper.toResponse(tenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Update tenant", description = "Updates an existing tenant with new details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
            ApiResponse(responseCode = "409", description = "Subdomain conflict"),
        ],
    )
    @PutMapping("/{tenantId}")
    suspend fun updateTenant(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
        @Valid @RequestBody request: UpdateTenantRequest,
    ): ResponseEntity<TenantResponse> {
        val existingTenant = tenantService.findById(tenantId)
        val updatedTenant = TenantMapper.toEntity(existingTenant, request)
        val savedTenant = tenantService.updateTenant(tenantId, updatedTenant)
        val response = TenantMapper.toResponse(savedTenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Update tenant status", description = "Updates the status of an existing tenant")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant status updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid status"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
            ApiResponse(responseCode = "403", description = "Operation not allowed"),
        ],
    )
    @PatchMapping("/{tenantId}/status")
    suspend fun updateTenantStatus(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
        @Valid @RequestBody request: TenantStatusUpdateRequest,
    ): ResponseEntity<TenantResponse> {
        val updatedTenant = tenantService.updateTenantStatus(tenantId, request.status)
        val response = TenantMapper.toResponse(updatedTenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Activate tenant", description = "Activates a tenant (sets status to ACTIVE)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant activated successfully"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
        ],
    )
    @PatchMapping("/{tenantId}/activate")
    suspend fun activateTenant(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
    ): ResponseEntity<TenantResponse> {
        val updatedTenant = tenantService.activateTenant(tenantId)
        val response = TenantMapper.toResponse(updatedTenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Suspend tenant", description = "Suspends a tenant (sets status to SUSPENDED)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant suspended successfully"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
            ApiResponse(responseCode = "403", description = "Tenant already suspended"),
        ],
    )
    @PatchMapping("/{tenantId}/suspend")
    suspend fun suspendTenant(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
        @Parameter(description = "Reason for suspension") @RequestParam(required = false) reason: String?,
    ): ResponseEntity<TenantResponse> {
        val updatedTenant = tenantService.suspendTenant(tenantId, reason)
        val response = TenantMapper.toResponse(updatedTenant)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Delete tenant", description = "Soft deletes a tenant (sets status to INACTIVE)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Tenant deleted successfully"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
            ApiResponse(responseCode = "403", description = "Tenant already inactive"),
        ],
    )
    @DeleteMapping("/{tenantId}")
    suspend fun deleteTenant(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
    ): ResponseEntity<Void> {
        tenantService.deleteTenant(tenantId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Get tenants by status", description = "Retrieves all tenants with the specified status")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenants retrieved successfully"),
        ],
    )
    @GetMapping("/status/{status}")
    suspend fun getTenantsByStatus(
        @Parameter(description = "Tenant status") @PathVariable status: TenantStatus,
    ): ResponseEntity<List<TenantResponse>> {
        val tenants = tenantService.findTenantsByStatus(status)
        val response = TenantMapper.toResponseList(tenants)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get active tenants", description = "Retrieves all active tenants")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Active tenants retrieved successfully"),
        ],
    )
    @GetMapping("/active")
    suspend fun getActiveTenants(): ResponseEntity<List<TenantResponse>> {
        val tenants = tenantService.findActiveTenants()
        val response = TenantMapper.toResponseList(tenants)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Check subdomain availability", description = "Checks if a subdomain is available for use")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Availability check completed"),
        ],
    )
    @GetMapping("/subdomain/{subdomain}/availability")
    suspend fun checkSubdomainAvailability(
        @Parameter(description = "Subdomain to check") @PathVariable subdomain: String,
    ): ResponseEntity<SubdomainAvailabilityResponse> {
        val isAvailable = tenantService.isSubdomainAvailable(subdomain)
        val response = SubdomainAvailabilityResponse(subdomain, isAvailable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get tenant summary", description = "Retrieves summary statistics about tenants")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
        ],
    )
    @GetMapping("/summary")
    suspend fun getTenantSummary(): ResponseEntity<TenantSummaryResponse> {
        val activeCount = tenantService.countTenantsByStatus(TenantStatus.ACTIVE)
        val suspendedCount = tenantService.countTenantsByStatus(TenantStatus.SUSPENDED)
        val inactiveCount = tenantService.countTenantsByStatus(TenantStatus.INACTIVE)
        val totalCount = activeCount + suspendedCount + inactiveCount

        val response =
            TenantSummaryResponse(
                totalTenants = totalCount,
                activeTenantsCount = activeCount,
                suspendedTenantsCount = suspendedCount,
                inactiveTenantsCount = inactiveCount,
            )
        return ResponseEntity.ok(response)
    }
}
