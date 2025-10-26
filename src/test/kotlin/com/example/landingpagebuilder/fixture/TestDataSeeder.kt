package com.example.landingpagebuilder.fixture

import com.example.landingpagebuilder.domain.model.Page
import com.example.landingpagebuilder.domain.model.Tenant
import com.example.landingpagebuilder.domain.model.TenantFeature
import com.example.landingpagebuilder.domain.model.TenantStatus
import com.example.landingpagebuilder.repository.PageRepository
import com.example.landingpagebuilder.repository.TenantRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class TestDataSeeder(
    private val tenantRepository: TenantRepository,
    private val pageRepository: PageRepository,
) {
    fun seedAll() =
        runBlocking {
            val tenants = seedTenants()
            seedPages(tenants)
        }

    fun seedTenants(): List<Tenant> =
        runBlocking {
            val tenants =
                listOf(
                    TestDataFactory.createActiveTenant(
                        subdomain = "acme-corp",
                        features =
                            setOf(
                                TenantFeature.BASIC_EDITOR,
                                TenantFeature.ADVANCED_EDITOR,
                                TenantFeature.CUSTOM_CSS,
                            ),
                    ),
                    TestDataFactory.createActiveTenant(
                        subdomain = "startup-inc",
                        features =
                            setOf(
                                TenantFeature.BASIC_EDITOR,
                                TenantFeature.ANALYTICS,
                                TenantFeature.API_ACCESS,
                            ),
                    ),
                    TestDataFactory.createTenantWithCustomDomain(
                        subdomain = "enterprise-co",
                        customDomain = "enterprise.example.com",
                    ),
                    TestDataFactory.createSuspendedTenant(
                        subdomain = "suspended-tenant",
                    ),
                    TestDataFactory.createTenant(
                        subdomain = "pending-tenant",
                        status = TenantStatus.PENDING_ACTIVATION,
                    ),
                )

            tenants.map { tenantRepository.save(it) }
        }

    fun seedPages(tenants: List<Tenant>): List<Page> =
        runBlocking {
            val pages = mutableListOf<Page>()

            tenants.filter { it.status == TenantStatus.ACTIVE }.forEach { tenant ->
                tenant.id?.let { tenantId ->
                    pages.addAll(
                        listOf(
                            TestDataFactory.createHomepage(tenantId = tenantId),
                            TestDataFactory.createPublishedPage(
                                tenantId = tenantId,
                                slug = "about",
                                title = "About Us",
                            ),
                            TestDataFactory.createPublishedPage(
                                tenantId = tenantId,
                                slug = "contact",
                                title = "Contact Us",
                            ),
                            TestDataFactory.createComplexPage(
                                tenantId = tenantId,
                                slug = "features",
                                title = "Our Features",
                            ).copy(status = com.example.landingpagebuilder.domain.model.PageStatus.PUBLISHED),
                            TestDataFactory.createPage(
                                tenantId = tenantId,
                                slug = "draft-page",
                                title = "Draft Page",
                                status = com.example.landingpagebuilder.domain.model.PageStatus.DRAFT,
                            ),
                        ),
                    )
                }
            }

            pages.map { pageRepository.save(it) }
        }

    fun clearAll() =
        runBlocking {
            pageRepository.deleteAll()
            tenantRepository.deleteAll()
        }

    fun seedMinimalData(): Pair<Tenant, Page> =
        runBlocking {
            val tenant =
                tenantRepository.save(
                    TestDataFactory.createActiveTenant(subdomain = "test-minimal"),
                )
            val page =
                pageRepository.save(
                    TestDataFactory.createHomepage(tenantId = tenant.id!!),
                )
            tenant to page
        }

    fun seedTenantWithPages(
        subdomain: String = "test-tenant",
        pageCount: Int = 3,
    ): Pair<Tenant, List<Page>> =
        runBlocking {
            val tenant =
                tenantRepository.save(
                    TestDataFactory.createActiveTenant(subdomain = subdomain),
                )
            val pages =
                (1..pageCount).map { index ->
                    pageRepository.save(
                        TestDataFactory.createPublishedPage(
                            tenantId = tenant.id!!,
                            slug = "page-$index",
                            title = "Page $index",
                        ),
                    )
                }
            tenant to pages
        }
}
