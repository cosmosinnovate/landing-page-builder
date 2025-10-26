package com.example.landingpagebuilder.service

import com.example.landingpagebuilder.domain.model.Tenant
import com.example.landingpagebuilder.domain.model.TenantStatus
import com.example.landingpagebuilder.exception.TenantAlreadyExistsException
import com.example.landingpagebuilder.exception.TenantNotFoundException
import com.example.landingpagebuilder.exception.TenantOperationNotAllowedException
import com.example.landingpagebuilder.exception.TenantValidationException
import com.example.landingpagebuilder.repository.TenantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TenantService(
    private val tenantRepository: TenantRepository,
) {
    /**
     * Create a new tenant with validation
     */
    suspend fun createTenant(tenant: Tenant): Tenant =
        withContext(Dispatchers.IO) {
            // Validate tenant data
            validateTenant(tenant)

            // Check if subdomain already exists
            if (tenantRepository.existsBySubdomain(tenant.subdomain)) {
                throw TenantAlreadyExistsException(tenant.subdomain)
            }

            // Save the new tenant
            // Ensure new entity
            val savedTenant =
                tenantRepository.save(
                    tenant.copy(
                        id = null,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    ),
                )

            savedTenant
        }

    /**
     * Find tenant by ID
     */
    suspend fun findById(tenantId: String): Tenant =
        withContext(Dispatchers.IO) {
            tenantRepository.findById(tenantId)
                .orElseThrow { TenantNotFoundException(tenantId) }
        }

    /**
     * Find tenant by subdomain (most common lookup)
     */
    suspend fun findBySubdomain(subdomain: String): Tenant =
        withContext(Dispatchers.IO) {
            tenantRepository.findBySubdomain(subdomain)
                .orElseThrow { TenantNotFoundException(subdomain, true) }
        }

    /**
     * Find tenant by custom domain
     */
    suspend fun findByCustomDomain(customDomain: String): Tenant =
        withContext(Dispatchers.IO) {
            tenantRepository.findByCustomDomain(customDomain)
                .orElseThrow { TenantNotFoundException("Tenant not found with custom domain: $customDomain") }
        }

    /**
     * Update existing tenant
     */
    suspend fun updateTenant(
        tenantId: String,
        updates: Tenant,
    ): Tenant =
        withContext(Dispatchers.IO) {
            val existingTenant = findById(tenantId)

            // Validate that subdomain change doesn't conflict
            if (updates.subdomain != existingTenant.subdomain &&
                tenantRepository.existsBySubdomain(updates.subdomain)
            ) {
                throw TenantAlreadyExistsException(updates.subdomain)
            }

            // Validate updated tenant data
            validateTenant(updates)

            val updatedTenant =
                updates.copy(
                    id = existingTenant.id,
                    createdAt = existingTenant.createdAt,
                    updatedAt = LocalDateTime.now(),
                )

            tenantRepository.save(updatedTenant)
        }

    /**
     * Update tenant status
     */
    suspend fun updateTenantStatus(
        tenantId: String,
        newStatus: TenantStatus,
    ): Tenant =
        withContext(Dispatchers.IO) {
            val tenant = findById(tenantId)

            val updatedTenant =
                tenant.copy(
                    status = newStatus,
                    updatedAt = LocalDateTime.now(),
                )

            tenantRepository.save(updatedTenant)
        }

    /**
     * Activate tenant (change status to ACTIVE)
     */
    suspend fun activateTenant(tenantId: String): Tenant =
        withContext(Dispatchers.IO) {
            updateTenantStatus(tenantId, TenantStatus.ACTIVE)
        }

    /**
     * Suspend tenant (change status to SUSPENDED)
     */
    suspend fun suspendTenant(
        tenantId: String,
        reason: String? = null,
    ): Tenant =
        withContext(Dispatchers.IO) {
            val tenant = findById(tenantId)

            if (tenant.status == TenantStatus.SUSPENDED) {
                throw TenantOperationNotAllowedException("Tenant is already suspended")
            }

            updateTenantStatus(tenantId, TenantStatus.SUSPENDED)
        }

    /**
     * Delete tenant (soft delete by setting status to INACTIVE)
     */
    suspend fun deleteTenant(tenantId: String): Unit =
        withContext(Dispatchers.IO) {
            val tenant = findById(tenantId)

            if (tenant.status == TenantStatus.INACTIVE) {
                throw TenantOperationNotAllowedException("Tenant is already inactive")
            }

            updateTenantStatus(tenantId, TenantStatus.INACTIVE)
            Unit
        }

    /**
     * Get all tenants by status
     */
    suspend fun findTenantsByStatus(status: TenantStatus): List<Tenant> =
        withContext(Dispatchers.IO) {
            tenantRepository.findByStatusOrderByCreatedAtDesc(status)
        }

    /**
     * Get all active tenants
     */
    suspend fun findActiveTenants(): List<Tenant> =
        withContext(Dispatchers.IO) {
            tenantRepository.findActiveTenants()
        }

    /**
     * Count tenants by status
     */
    suspend fun countTenantsByStatus(status: TenantStatus): Long =
        withContext(Dispatchers.IO) {
            tenantRepository.countByStatus(status)
        }

    /**
     * Check if subdomain is available
     */
    suspend fun isSubdomainAvailable(subdomain: String): Boolean =
        withContext(Dispatchers.IO) {
            !tenantRepository.existsBySubdomain(subdomain)
        }

    /**
     * Validate tenant data
     */
    private fun validateTenant(tenant: Tenant) {
        // Validate subdomain format
        if (!isValidSubdomain(tenant.subdomain)) {
            throw TenantValidationException("Invalid subdomain format: ${tenant.subdomain}")
        }

        // Validate email format
        if (!isValidEmail(tenant.email)) {
            throw TenantValidationException("Invalid email format: ${tenant.email}")
        }

        // Validate name is not empty
        if (tenant.name.isBlank()) {
            throw TenantValidationException("Tenant name cannot be empty")
        }

        // Validate custom domain if provided
        tenant.settings.customDomain?.let { customDomain ->
            if (!isValidDomain(customDomain)) {
                throw TenantValidationException("Invalid custom domain format: $customDomain")
            }
        }
    }

    /**
     * Validate subdomain format (alphanumeric, lowercase, hyphens allowed, 3-63 chars)
     */
    private fun isValidSubdomain(subdomain: String): Boolean {
        val subdomainRegex = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$".toRegex()
        return subdomain.matches(subdomainRegex) && !subdomain.contains("--")
    }

    /**
     * Basic email validation
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    /**
     * Basic domain validation
     */
    private fun isValidDomain(domain: String): Boolean {
        val domainRegex = "^[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9]\\.[a-zA-Z]{2,}$".toRegex()
        return domain.matches(domainRegex)
    }
}
