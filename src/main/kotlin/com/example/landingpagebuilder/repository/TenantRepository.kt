package com.example.landingpagebuilder.repository

import com.example.landingpagebuilder.domain.model.Tenant
import com.example.landingpagebuilder.domain.model.TenantStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TenantRepository : MongoRepository<Tenant, String> {
    /**
     * Find tenant by subdomain (unique constraint)
     */
    fun findBySubdomain(subdomain: String): Optional<Tenant>

    /**
     * Find all tenants by status
     */
    fun findByStatus(status: TenantStatus): List<Tenant>

    /**
     * Find tenants by status with pagination
     */
    @Query("{ 'status': ?0 }")
    fun findByStatusOrderByCreatedAtDesc(status: TenantStatus): List<Tenant>

    /**
     * Check if subdomain exists (for validation)
     */
    fun existsBySubdomain(subdomain: String): Boolean

    /**
     * Find active tenants only
     */
    @Query("{ 'status': 'ACTIVE' }")
    fun findActiveTenants(): List<Tenant>

    /**
     * Find tenants created after a specific date
     */
    @Query("{ 'createdAt': { \$gte: ?0 } }")
    fun findTenantsCreatedAfter(date: java.time.LocalDateTime): List<Tenant>

    /**
     * Count tenants by status
     */
    fun countByStatus(status: TenantStatus): Long

    /**
     * Find tenants by email (for user lookup)
     */
    fun findByEmail(email: String): List<Tenant>

    /**
     * Find tenants by custom domain
     */
    @Query("{ 'settings.customDomain': ?0 }")
    fun findByCustomDomain(customDomain: String): Optional<Tenant>
}
