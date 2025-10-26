package com.example.landingpagebuilder.service

import com.example.landingpagebuilder.domain.model.Page
import com.example.landingpagebuilder.domain.model.PageStatus
import com.example.landingpagebuilder.exception.PageNotFoundException
import com.example.landingpagebuilder.repository.PageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class PageService(
    private val pageRepository: PageRepository,
) {
    /**
     * Find published page by tenant ID and slug for public access
     */
    suspend fun findPublishedPage(
        tenantId: String,
        slug: String,
    ): Page =
        withContext(Dispatchers.IO) {
            // Handle homepage slug variations
            val normalizedSlug = normalizeSlug(slug)

            // Try to find exact match first
            pageRepository.findPublishedByTenantIdAndSlug(tenantId, normalizedSlug)
                .orElse(
                    // If not found and it's a homepage request, try homepage candidates
                    if (isHomepageSlug(normalizedSlug)) {
                        findHomepage(tenantId)
                    } else {
                        null
                    },
                )
                ?: throw PageNotFoundException("Published page not found with slug: $slug for tenant: $tenantId")
        }

    /**
     * Find homepage for a tenant (prioritizes 'home' > 'index' > '')
     */
    suspend fun findHomepage(tenantId: String): Page? =
        withContext(Dispatchers.IO) {
            val candidates = pageRepository.findHomepageCandidates(tenantId)

            // Prioritize homepage candidates
            candidates.find { it.slug == "home" }
                ?: candidates.find { it.slug == "index" }
                ?: candidates.find { it.slug == "" }
                ?: candidates.firstOrNull()
        }

    /**
     * Find page by ID (for internal use)
     */
    suspend fun findById(pageId: String): Page =
        withContext(Dispatchers.IO) {
            pageRepository.findById(pageId)
                .orElseThrow { PageNotFoundException("Page not found with ID: $pageId") }
        }

    /**
     * Find page by tenant ID and slug (any status)
     */
    suspend fun findByTenantAndSlug(
        tenantId: String,
        slug: String,
    ): Page =
        withContext(Dispatchers.IO) {
            pageRepository.findByTenantIdAndSlug(tenantId, slug)
                .orElseThrow { PageNotFoundException("Page not found with slug: $slug for tenant: $tenantId") }
        }

    /**
     * Get all pages for a tenant
     */
    suspend fun findPagesByTenant(tenantId: String): List<Page> =
        withContext(Dispatchers.IO) {
            pageRepository.findByTenantIdOrderByCreatedAtDesc(tenantId)
        }

    /**
     * Get pages by tenant and status
     */
    suspend fun findPagesByTenantAndStatus(
        tenantId: String,
        status: PageStatus,
    ): List<Page> =
        withContext(Dispatchers.IO) {
            pageRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, status)
        }

    /**
     * Check if slug is available for a tenant
     */
    suspend fun isSlugAvailable(
        tenantId: String,
        slug: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            !pageRepository.existsByTenantIdAndSlug(tenantId, normalizeSlug(slug))
        }

    /**
     * Create a new page
     */
    suspend fun createPage(page: Page): Page =
        withContext(Dispatchers.IO) {
            // Normalize slug before saving
            // Ensure new entity
            val normalizedPage =
                page.copy(
                    slug = normalizeSlug(page.slug),
                    id = null,
                )

            // Check if slug already exists
            if (!isSlugAvailable(page.tenantId, normalizedPage.slug)) {
                throw IllegalArgumentException("Slug '${normalizedPage.slug}' already exists for this tenant")
            }

            pageRepository.save(normalizedPage)
        }

    /**
     * Update an existing page
     */
    suspend fun updatePage(
        pageId: String,
        updates: Page,
    ): Page =
        withContext(Dispatchers.IO) {
            val existingPage = findById(pageId)

            // Check slug availability if slug is being changed
            if (updates.slug != existingPage.slug &&
                !isSlugAvailable(existingPage.tenantId, updates.slug)
            ) {
                throw IllegalArgumentException("Slug '${updates.slug}' already exists for this tenant")
            }

            // Don't allow tenant change
            // Preserve creation date
            val updatedPage =
                updates.copy(
                    id = existingPage.id,
                    tenantId = existingPage.tenantId,
                    createdAt = existingPage.createdAt,
                    slug = normalizeSlug(updates.slug),
                )

            pageRepository.save(updatedPage)
        }

    /**
     * Publish a page
     */
    suspend fun publishPage(pageId: String): Page =
        withContext(Dispatchers.IO) {
            val page = findById(pageId)
            val publishedPage =
                page.copy(
                    status = PageStatus.PUBLISHED,
                    publishedAt = java.time.LocalDateTime.now(),
                )
            pageRepository.save(publishedPage)
        }

    /**
     * Unpublish a page (set to draft)
     */
    suspend fun unpublishPage(pageId: String): Page =
        withContext(Dispatchers.IO) {
            val page = findById(pageId)
            val draftPage =
                page.copy(
                    status = PageStatus.DRAFT,
                    publishedAt = null,
                )
            pageRepository.save(draftPage)
        }

    /**
     * Delete a page
     */
    suspend fun deletePage(pageId: String): Unit =
        withContext(Dispatchers.IO) {
            val page = findById(pageId)
            pageRepository.delete(page)
        }

    /**
     * Normalize slug for consistency
     */
    private fun normalizeSlug(slug: String): String =
        slug.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9-]"), "-")
            .replace(Regex("-+"), "-")
            .removePrefix("-")
            .removeSuffix("-")

    /**
     * Check if slug is a homepage variant
     */
    private fun isHomepageSlug(slug: String): Boolean = slug in listOf("", "home", "index")
}
