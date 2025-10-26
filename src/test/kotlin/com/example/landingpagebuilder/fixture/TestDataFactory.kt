package com.example.landingpagebuilder.fixture

import com.example.landingpagebuilder.domain.model.BlockStyling
import com.example.landingpagebuilder.domain.model.BlockType
import com.example.landingpagebuilder.domain.model.ContentBlock
import com.example.landingpagebuilder.domain.model.ContentSection
import com.example.landingpagebuilder.domain.model.DesignSettings
import com.example.landingpagebuilder.domain.model.Page
import com.example.landingpagebuilder.domain.model.PageContent
import com.example.landingpagebuilder.domain.model.PageStatus
import com.example.landingpagebuilder.domain.model.SectionSettings
import com.example.landingpagebuilder.domain.model.SectionType
import com.example.landingpagebuilder.domain.model.SeoSettings
import com.example.landingpagebuilder.domain.model.Spacing
import com.example.landingpagebuilder.domain.model.Tenant
import com.example.landingpagebuilder.domain.model.TenantFeature
import com.example.landingpagebuilder.domain.model.TenantSettings
import com.example.landingpagebuilder.domain.model.TenantStatus
import com.example.landingpagebuilder.domain.model.TextAlign
import java.time.LocalDateTime
import java.util.UUID

object TestDataFactory {
    fun createTenant(
        id: String? = null,
        subdomain: String = "test-tenant-${UUID.randomUUID().toString().substring(0, 8)}",
        name: String = "Test Tenant",
        email: String = "test@example.com",
        status: TenantStatus = TenantStatus.ACTIVE,
        settings: TenantSettings = createTenantSettings(),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now(),
    ): Tenant =
        Tenant(
            id = id,
            subdomain = subdomain,
            name = name,
            email = email,
            status = status,
            settings = settings,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun createTenantSettings(
        customDomain: String? = null,
        logoUrl: String? = null,
        primaryColor: String = "#007bff",
        secondaryColor: String = "#6c757d",
        allowCustomStyling: Boolean = true,
        maxPages: Int = 10,
        storageLimit: Long = 1048576L,
        features: Set<TenantFeature> = setOf(TenantFeature.BASIC_EDITOR),
    ): TenantSettings =
        TenantSettings(
            customDomain = customDomain,
            logoUrl = logoUrl,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            allowCustomStyling = allowCustomStyling,
            maxPages = maxPages,
            storageLimit = storageLimit,
            features = features,
        )

    fun createPage(
        id: String? = null,
        tenantId: String = "test-tenant-id",
        slug: String = "test-page",
        title: String = "Test Page",
        metaDescription: String? = "Test page description",
        metaKeywords: String? = "test, keywords",
        status: PageStatus = PageStatus.DRAFT,
        content: PageContent = createPageContent(),
        seoSettings: SeoSettings = createSeoSettings(),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now(),
        publishedAt: LocalDateTime? = null,
    ): Page =
        Page(
            id = id,
            tenantId = tenantId,
            slug = slug,
            title = title,
            metaDescription = metaDescription,
            metaKeywords = metaKeywords,
            status = status,
            content = content,
            seoSettings = seoSettings,
            createdAt = createdAt,
            updatedAt = updatedAt,
            publishedAt = publishedAt,
        )

    fun createPageContent(
        sections: List<ContentSection> = emptyList(),
        designSettings: DesignSettings = createDesignSettings(),
    ): PageContent =
        PageContent(
            sections = sections,
            designSettings = designSettings,
        )

    fun createContentSection(
        id: String = UUID.randomUUID().toString(),
        type: SectionType = SectionType.CONTENT,
        blocks: List<ContentBlock> = emptyList(),
        settings: SectionSettings = createSectionSettings(),
    ): ContentSection =
        ContentSection(
            id = id,
            type = type,
            blocks = blocks,
            settings = settings,
        )

    fun createContentBlock(
        id: String = UUID.randomUUID().toString(),
        type: BlockType = BlockType.PARAGRAPH,
        content: Map<String, Any> = emptyMap(),
        styling: BlockStyling = createBlockStyling(),
    ): ContentBlock =
        ContentBlock(
            id = id,
            type = type,
            content = content,
            styling = styling,
        )

    fun createSectionSettings(
        backgroundColor: String? = null,
        padding: Spacing = createSpacing(),
        margin: Spacing = createSpacing(),
        fullWidth: Boolean = false,
        customCss: String? = null,
    ): SectionSettings =
        SectionSettings(
            backgroundColor = backgroundColor,
            padding = padding,
            margin = margin,
            fullWidth = fullWidth,
            customCss = customCss,
        )

    fun createBlockStyling(
        textAlign: TextAlign = TextAlign.LEFT,
        fontSize: String? = null,
        fontWeight: String? = null,
        color: String? = null,
        backgroundColor: String? = null,
        padding: Spacing = createSpacing(),
        margin: Spacing = createSpacing(),
        borderRadius: String? = null,
        customCss: String? = null,
    ): BlockStyling =
        BlockStyling(
            textAlign = textAlign,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color,
            backgroundColor = backgroundColor,
            padding = padding,
            margin = margin,
            borderRadius = borderRadius,
            customCss = customCss,
        )

    fun createSpacing(
        top: String = "0",
        right: String = "0",
        bottom: String = "0",
        left: String = "0",
    ): Spacing =
        Spacing(
            top = top,
            right = right,
            bottom = bottom,
            left = left,
        )

    fun createDesignSettings(
        theme: String = "default",
        primaryColor: String = "#007bff",
        secondaryColor: String = "#6c757d",
        fontFamily: String = "Arial, sans-serif",
        containerWidth: String = "1200px",
        customCss: String? = null,
    ): DesignSettings =
        DesignSettings(
            theme = theme,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            fontFamily = fontFamily,
            containerWidth = containerWidth,
            customCss = customCss,
        )

    fun createSeoSettings(
        ogTitle: String? = null,
        ogDescription: String? = null,
        ogImage: String? = null,
        twitterCard: String = "summary",
        canonicalUrl: String? = null,
        noIndex: Boolean = false,
        noFollow: Boolean = false,
    ): SeoSettings =
        SeoSettings(
            ogTitle = ogTitle,
            ogDescription = ogDescription,
            ogImage = ogImage,
            twitterCard = twitterCard,
            canonicalUrl = canonicalUrl,
            noIndex = noIndex,
            noFollow = noFollow,
        )

    fun createComplexPage(
        tenantId: String = "test-tenant-id",
        slug: String = "complex-page",
        title: String = "Complex Landing Page",
    ): Page {
        val heroSection =
            createContentSection(
                type = SectionType.HERO,
                blocks =
                    listOf(
                        createContentBlock(
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Welcome to Our Service",
                                    "level" to 1,
                                ),
                            styling =
                                createBlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "3rem",
                                    fontWeight = "bold",
                                ),
                        ),
                        createContentBlock(
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to "Build amazing landing pages in minutes",
                                ),
                            styling =
                                createBlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "1.5rem",
                                ),
                        ),
                        createContentBlock(
                            type = BlockType.BUTTON,
                            content =
                                mapOf(
                                    "text" to "Get Started",
                                    "url" to "/signup",
                                    "style" to "primary",
                                ),
                        ),
                    ),
                settings =
                    createSectionSettings(
                        backgroundColor = "#f8f9fa",
                        padding = createSpacing(top = "60px", bottom = "60px"),
                    ),
            )

        val featuresSection =
            createContentSection(
                type = SectionType.FEATURES,
                blocks =
                    listOf(
                        createContentBlock(
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Features",
                                    "level" to 2,
                                ),
                        ),
                        createContentBlock(
                            type = BlockType.LIST,
                            content =
                                mapOf(
                                    "items" to
                                        listOf(
                                            "Easy to use editor",
                                            "Mobile responsive",
                                            "SEO optimized",
                                            "Fast performance",
                                        ),
                                ),
                        ),
                    ),
            )

        val ctaSection =
            createContentSection(
                type = SectionType.CTA,
                blocks =
                    listOf(
                        createContentBlock(
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Ready to get started?",
                                    "level" to 2,
                                ),
                            styling = createBlockStyling(textAlign = TextAlign.CENTER),
                        ),
                        createContentBlock(
                            type = BlockType.BUTTON,
                            content =
                                mapOf(
                                    "text" to "Sign Up Now",
                                    "url" to "/signup",
                                    "style" to "primary",
                                ),
                        ),
                    ),
                settings =
                    createSectionSettings(
                        backgroundColor = "#007bff",
                        padding = createSpacing(top = "40px", bottom = "40px"),
                    ),
            )

        return createPage(
            tenantId = tenantId,
            slug = slug,
            title = title,
            metaDescription = "A comprehensive landing page with all features",
            metaKeywords = "landing, page, builder, features",
            content =
                createPageContent(
                    sections = listOf(heroSection, featuresSection, ctaSection),
                    designSettings =
                        createDesignSettings(
                            theme = "modern",
                            primaryColor = "#007bff",
                            secondaryColor = "#6c757d",
                        ),
                ),
            seoSettings =
                createSeoSettings(
                    ogTitle = "Complex Landing Page",
                    ogDescription = "A comprehensive landing page with all features",
                    ogImage = "https://example.com/og-image.jpg",
                    twitterCard = "summary_large_image",
                ),
        )
    }

    fun createPublishedPage(
        tenantId: String = "test-tenant-id",
        slug: String = "published-page",
        title: String = "Published Page",
    ): Page =
        createPage(
            tenantId = tenantId,
            slug = slug,
            title = title,
            status = PageStatus.PUBLISHED,
            publishedAt = LocalDateTime.now(),
        )

    fun createHomepage(tenantId: String = "test-tenant-id"): Page =
        createPublishedPage(
            tenantId = tenantId,
            slug = "home",
            title = "Home",
        )

    fun createActiveTenant(
        subdomain: String = "active-tenant",
        features: Set<TenantFeature> =
            setOf(
                TenantFeature.BASIC_EDITOR,
                TenantFeature.ADVANCED_EDITOR,
            ),
    ): Tenant =
        createTenant(
            subdomain = subdomain,
            status = TenantStatus.ACTIVE,
            settings = createTenantSettings(features = features),
        )

    fun createSuspendedTenant(subdomain: String = "suspended-tenant"): Tenant =
        createTenant(
            subdomain = subdomain,
            status = TenantStatus.SUSPENDED,
        )

    fun createTenantWithCustomDomain(
        subdomain: String = "custom-domain-tenant",
        customDomain: String = "example.com",
    ): Tenant =
        createTenant(
            subdomain = subdomain,
            settings =
                createTenantSettings(
                    customDomain = customDomain,
                    features = setOf(TenantFeature.CUSTOM_DOMAIN, TenantFeature.ADVANCED_EDITOR),
                ),
        )
}
