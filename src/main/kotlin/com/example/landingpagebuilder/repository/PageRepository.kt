package com.example.landingpagebuilder.repository

import com.example.landingpagebuilder.domain.model.Page
import com.example.landingpagebuilder.domain.model.PageStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PageRepository : MongoRepository<Page, String> {
    /**
     * Find page by tenant ID and slug
     */
    fun findByTenantIdAndSlug(
        tenantId: String,
        slug: String,
    ): Optional<Page>

    /**
     * Find published page by tenant ID and slug
     */
    @Query("{ 'tenantId': ?0, 'slug': ?1, 'status': 'PUBLISHED' }")
    fun findPublishedByTenantIdAndSlug(
        tenantId: String,
        slug: String,
    ): Optional<Page>

    /**
     * Find all pages by tenant ID
     */
    fun findByTenantIdOrderByCreatedAtDesc(tenantId: String): List<Page>

    /**
     * Find pages by tenant ID and status
     */
    fun findByTenantIdAndStatusOrderByCreatedAtDesc(
        tenantId: String,
        status: PageStatus,
    ): List<Page>

    /**
     * Check if slug exists for a tenant
     */
    fun existsByTenantIdAndSlug(
        tenantId: String,
        slug: String,
    ): Boolean

    /**
     * Find homepage candidates for tenant (common home page slugs)
     */
    @Query("{ 'tenantId': ?0, 'slug': { \$in: ['home', 'index', ''] }, 'status': 'PUBLISHED' }")
    fun findHomepageCandidates(tenantId: String): List<Page>

    /**
     * Count pages by tenant ID and status
     */
    fun countByTenantIdAndStatus(
        tenantId: String,
        status: PageStatus,
    ): Long

    /**
     * Find pages created after a specific date
     */
    @Query("{ 'tenantId': ?0, 'createdAt': { \$gte: ?1 } }")
    fun findByTenantIdAndCreatedAtAfter(
        tenantId: String,
        date: java.time.LocalDateTime,
    ): List<Page>
}
