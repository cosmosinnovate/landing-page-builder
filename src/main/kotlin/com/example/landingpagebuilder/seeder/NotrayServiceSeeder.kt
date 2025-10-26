package com.example.landingpagebuilder.seeder

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
import com.example.landingpagebuilder.repository.PageRepository
import com.example.landingpagebuilder.repository.TenantRepository
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
@Profile("seed-notray")
class NotrayServiceSeeder(
    private val tenantRepository: TenantRepository,
    private val pageRepository: PageRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) =
        runBlocking {
            println("🌱 Starting NOTRAY SERVICE seeding...")

            // Create tenant
            val tenant = createNotrayTenant()
            val savedTenant = tenantRepository.save(tenant)
            println("✅ Created tenant: ${savedTenant.subdomain}")

            val tenantId = savedTenant.id!!

            // Create pages
            val pages =
                listOf(
                    createHomepage(tenantId),
                    createAboutPage(tenantId),
                    createFeaturesPage(tenantId),
                    createPricingPage(tenantId),
                    createContactPage(tenantId),
                )

            pages.forEach { page ->
                val savedPage = pageRepository.save(page)
                println("✅ Created page: ${savedPage.slug} (${savedPage.status})")
            }

            println("🎉 NOTRAY SERVICE seeding completed!")
            println("📍 Access at: http://localhost:8082/public/sites/notray-service")
        }

    private fun createNotrayTenant(): Tenant =
        Tenant(
            subdomain = "notray-service",
            name = "Notray Service",
            email = "hello@notrayservice.com",
            status = TenantStatus.ACTIVE,
            settings =
                TenantSettings(
                    customDomain = null,
                    logoUrl = "https://via.placeholder.com/150x50/4F46E5/FFFFFF?text=NOTRAY",
                    primaryColor = "#4F46E5",
                    secondaryColor = "#10B981",
                    allowCustomStyling = true,
                    maxPages = 50,
                    storageLimit = 10485760L,
                    features =
                        setOf(
                            TenantFeature.ADVANCED_EDITOR,
                            TenantFeature.CUSTOM_CSS,
                            TenantFeature.ANALYTICS,
                            TenantFeature.API_ACCESS,
                        ),
                ),
        )

    private fun createHomepage(tenantId: String): Page {
        val heroSection =
            ContentSection(
                id = UUID.randomUUID().toString(),
                type = SectionType.HERO,
                blocks =
                    listOf(
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Real-Time Notifications That Work",
                                    "level" to 1,
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "3.5rem",
                                    fontWeight = "bold",
                                    color = "#1F2937",
                                    margin = Spacing(bottom = "20px"),
                                ),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "Deliver instant notifications across web, mobile, email, and SMS. " +
                                        "Notray Service provides enterprise-grade notification infrastructure " +
                                        "trusted by thousands of companies worldwide.",
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "1.25rem",
                                    color = "#6B7280",
                                    margin = Spacing(bottom = "40px"),
                                ),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.BUTTON,
                            content =
                                mapOf(
                                    "text" to "Start Free Trial",
                                    "url" to "/signup",
                                    "style" to "primary",
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "1.125rem",
                                    padding = Spacing(top = "16px", right = "32px", bottom = "16px", left = "32px"),
                                ),
                        ),
                    ),
                settings =
                    SectionSettings(
                        backgroundColor = "#F9FAFB",
                        padding = Spacing(top = "100px", bottom = "100px"),
                        fullWidth = true,
                    ),
            )

        val featuresSection =
            ContentSection(
                id = UUID.randomUUID().toString(),
                type = SectionType.FEATURES,
                blocks =
                    listOf(
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Powerful Features for Modern Apps",
                                    "level" to 2,
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "2.5rem",
                                    fontWeight = "bold",
                                    margin = Spacing(bottom = "60px"),
                                ),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "🚀 **Multi-Channel Delivery** - Send notifications via push, email, SMS, " +
                                        "and in-app messages from a single API",
                                ),
                            styling = BlockStyling(margin = Spacing(bottom = "20px")),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "⚡ **Lightning Fast** - Sub-100ms delivery with 99.99% uptime SLA " +
                                        "and global infrastructure",
                                ),
                            styling = BlockStyling(margin = Spacing(bottom = "20px")),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "🎯 **Smart Targeting** - Segment users, schedule notifications, and " +
                                        "personalize content with powerful rules",
                                ),
                            styling = BlockStyling(margin = Spacing(bottom = "20px")),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "📊 **Advanced Analytics** - Track delivery rates, engagement metrics, " +
                                        "and user behavior in real-time",
                                ),
                            styling = BlockStyling(margin = Spacing(bottom = "20px")),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "🔒 **Enterprise Security** - SOC 2 Type II certified with " +
                                        "end-to-end encryption and compliance",
                                ),
                            styling = BlockStyling(margin = Spacing(bottom = "20px")),
                        ),
                    ),
                settings =
                    SectionSettings(
                        padding = Spacing(top = "80px", bottom = "80px"),
                    ),
            )

        val statsSection =
            ContentSection(
                id = UUID.randomUUID().toString(),
                type = SectionType.CONTENT,
                blocks =
                    listOf(
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Trusted by Industry Leaders",
                                    "level" to 2,
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "2rem",
                                    margin = Spacing(bottom = "40px"),
                                ),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to
                                        "**5B+** notifications delivered monthly | **10K+** companies using Notray | " +
                                        "**99.99%** uptime guarantee",
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "1.25rem",
                                    color = "#4F46E5",
                                ),
                        ),
                    ),
                settings =
                    SectionSettings(
                        backgroundColor = "#EEF2FF",
                        padding = Spacing(top = "60px", bottom = "60px"),
                        fullWidth = true,
                    ),
            )

        val ctaSection =
            ContentSection(
                id = UUID.randomUUID().toString(),
                type = SectionType.CTA,
                blocks =
                    listOf(
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.HEADING,
                            content =
                                mapOf(
                                    "text" to "Ready to Get Started?",
                                    "level" to 2,
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "2.5rem",
                                    fontWeight = "bold",
                                    color = "#FFFFFF",
                                    margin = Spacing(bottom = "20px"),
                                ),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.PARAGRAPH,
                            content =
                                mapOf(
                                    "text" to "Join thousands of companies using Notray Service. Start your free 14-day trial today.",
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    fontSize = "1.25rem",
                                    color = "#E0E7FF",
                                    margin = Spacing(bottom = "40px"),
                                ),
                        ),
                        ContentBlock(
                            id = UUID.randomUUID().toString(),
                            type = BlockType.BUTTON,
                            content =
                                mapOf(
                                    "text" to "Start Free Trial →",
                                    "url" to "/signup",
                                    "style" to "secondary",
                                ),
                            styling =
                                BlockStyling(
                                    textAlign = TextAlign.CENTER,
                                    backgroundColor = "#FFFFFF",
                                    color = "#4F46E5",
                                    padding = Spacing(top = "16px", right = "40px", bottom = "16px", left = "40px"),
                                ),
                        ),
                    ),
                settings =
                    SectionSettings(
                        backgroundColor = "#4F46E5",
                        padding = Spacing(top = "80px", bottom = "80px"),
                        fullWidth = true,
                    ),
            )

        return Page(
            tenantId = tenantId,
            slug = "home",
            title = "Notray Service - Real-Time Notifications for Modern Apps",
            metaDescription =
                "Enterprise-grade notification infrastructure. Deliver instant notifications across web, mobile, " +
                    "email, and SMS with 99.99% uptime.",
            metaKeywords = "notifications, push notifications, email, SMS, real-time, notification service, API",
            status = PageStatus.PUBLISHED,
            content =
                PageContent(
                    sections = listOf(heroSection, featuresSection, statsSection, ctaSection),
                    designSettings =
                        DesignSettings(
                            theme = "modern",
                            primaryColor = "#4F46E5",
                            secondaryColor = "#10B981",
                            fontFamily = "Inter, system-ui, -apple-system, sans-serif",
                            containerWidth = "1200px",
                        ),
                ),
            seoSettings =
                SeoSettings(
                    ogTitle = "Notray Service - Real-Time Notifications",
                    ogDescription = "Enterprise-grade notification infrastructure trusted by 10,000+ companies worldwide.",
                    ogImage = "https://via.placeholder.com/1200x630/4F46E5/FFFFFF?text=Notray+Service",
                    twitterCard = "summary_large_image",
                    canonicalUrl = "https://notrayservice.com",
                ),
            publishedAt = LocalDateTime.now(),
        )
    }

    private fun createAboutPage(tenantId: String): Page {
        val sections =
            listOf(
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.HEADER,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "About Notray Service", "level" to 1),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "3rem",
                                        fontWeight = "bold",
                                        margin = Spacing(bottom = "20px"),
                                    ),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Building the future of real-time communication",
                                    ),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "1.5rem",
                                        color = "#6B7280",
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(top = "80px", bottom = "60px")),
                ),
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.CONTENT,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Our Mission", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "20px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Founded in 2020, Notray Service was born from a simple observation: " +
                                            "notifications are critical to modern applications, yet they're often an afterthought. " +
                                            "We set out to change that by building the most reliable, scalable, and developer-friendly " +
                                            "notification infrastructure on the planet.",
                                    ),
                                styling =
                                    BlockStyling(
                                        fontSize = "1.125rem",
                                        margin = Spacing(bottom = "30px"),
                                    ),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to
                                            "Today, we process over 5 billion notifications monthly for more than 10,000 companies " +
                                            "across 150 countries. From startups to Fortune 500 enterprises, developers trust " +
                                            "Notray Service to deliver their most critical messages.",
                                    ),
                                styling =
                                    BlockStyling(
                                        fontSize = "1.125rem",
                                        margin = Spacing(bottom = "40px"),
                                    ),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Our Values", "level" to 2),
                                styling = BlockStyling(margin = Spacing(top = "40px", bottom = "20px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.LIST,
                                content =
                                    mapOf(
                                        "items" to
                                            listOf(
                                                "**Reliability First** - Your notifications must get through. We maintain 99.99% uptime.",
                                                "**Developer Experience** - Simple APIs, comprehensive docs, and responsive support.",
                                                "**Privacy & Security** - SOC 2 Type II certified with enterprise-grade security.",
                                                "**Transparency** - Open communication about incidents, features, and roadmap.",
                                                "**Customer Success** - Your success is our success. We're here to help you grow.",
                                            ),
                                    ),
                                styling = BlockStyling(fontSize = "1.125rem"),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(top = "40px", bottom = "80px")),
                ),
            )

        return Page(
            tenantId = tenantId,
            slug = "about",
            title = "About Us - Notray Service",
            metaDescription =
                "Learn about Notray Service's mission to build the most reliable notification infrastructure " +
                    "for modern applications.",
            metaKeywords = "about notray, company, mission, values, notification service",
            status = PageStatus.PUBLISHED,
            content =
                PageContent(
                    sections = sections,
                    designSettings =
                        DesignSettings(
                            primaryColor = "#4F46E5",
                            secondaryColor = "#10B981",
                        ),
                ),
            seoSettings =
                SeoSettings(
                    ogTitle = "About Notray Service",
                    ogDescription = "Building the future of real-time communication",
                ),
            publishedAt = LocalDateTime.now(),
        )
    }

    private fun createFeaturesPage(tenantId: String): Page {
        val sections =
            listOf(
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.HEADER,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Features", "level" to 1),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "3rem",
                                        fontWeight = "bold",
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(top = "80px", bottom = "60px")),
                ),
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.FEATURES,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Multi-Channel Delivery", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Send notifications across push, email, SMS, and in-app messages. " +
                                            "One API, multiple channels, unlimited possibilities.",
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "30px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Advanced Segmentation", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Target users based on behavior, demographics, location, and custom attributes. " +
                                            "Create sophisticated campaigns with our visual segment builder.",
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "30px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Real-Time Analytics", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Track delivery rates, open rates, click-through rates, and conversions. " +
                                            "Make data-driven decisions with comprehensive analytics dashboards.",
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "30px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "A/B Testing", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Optimize your messaging with built-in A/B testing. Test different content, " +
                                            "timing, and channels to maximize engagement.",
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "30px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Template Management", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Create reusable templates with our drag-and-drop editor. Support for dynamic content, " +
                                            "personalization variables, and multi-language localization.",
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(bottom = "80px")),
                ),
            )

        return Page(
            tenantId = tenantId,
            slug = "features",
            title = "Features - Notray Service",
            metaDescription =
                "Explore Notray Service features: multi-channel delivery, advanced segmentation, real-time analytics, " +
                    "A/B testing, and more.",
            metaKeywords = "features, push notifications, email, SMS, analytics, segmentation",
            status = PageStatus.PUBLISHED,
            content = PageContent(sections = sections),
            seoSettings =
                SeoSettings(
                    ogTitle = "Features - Notray Service",
                    ogDescription = "Discover powerful features for modern notification infrastructure",
                ),
            publishedAt = LocalDateTime.now(),
        )
    }

    private fun createPricingPage(tenantId: String): Page {
        val sections =
            listOf(
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.HEADER,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Simple, Transparent Pricing", "level" to 1),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "3rem",
                                        fontWeight = "bold",
                                        margin = Spacing(bottom = "20px"),
                                    ),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Start free, scale as you grow. No hidden fees.",
                                    ),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "1.25rem",
                                        color = "#6B7280",
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(top = "80px", bottom = "60px")),
                ),
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.CONTENT,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Free Tier", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content = mapOf("text" to "**$0/month** - Perfect for getting started"),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.LIST,
                                content =
                                    mapOf(
                                        "items" to
                                            listOf(
                                                "10,000 notifications/month",
                                                "All channels (Push, Email, SMS, In-App)",
                                                "Basic analytics",
                                                "Community support",
                                                "7-day data retention",
                                            ),
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "40px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Growth", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content = mapOf("text" to "**$99/month** - For growing applications"),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.LIST,
                                content =
                                    mapOf(
                                        "items" to
                                            listOf(
                                                "500,000 notifications/month",
                                                "Advanced segmentation",
                                                "A/B testing",
                                                "Real-time analytics",
                                                "Email & chat support",
                                                "30-day data retention",
                                                "Custom branding",
                                            ),
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "40px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Enterprise", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content = mapOf("text" to "**Custom pricing** - For large-scale applications"),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.LIST,
                                content =
                                    mapOf(
                                        "items" to
                                            listOf(
                                                "Unlimited notifications",
                                                "Dedicated infrastructure",
                                                "99.99% SLA",
                                                "24/7 phone support",
                                                "Unlimited data retention",
                                                "SSO & advanced security",
                                                "Custom integrations",
                                                "Dedicated account manager",
                                            ),
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(bottom = "80px")),
                ),
            )

        return Page(
            tenantId = tenantId,
            slug = "pricing",
            title = "Pricing - Notray Service",
            metaDescription =
                "Simple, transparent pricing for Notray Service. Start free with 10,000 notifications/month. " +
                    "Scale as you grow.",
            metaKeywords = "pricing, plans, free tier, growth, enterprise, notification costs",
            status = PageStatus.PUBLISHED,
            content = PageContent(sections = sections),
            seoSettings =
                SeoSettings(
                    ogTitle = "Pricing - Notray Service",
                    ogDescription = "Simple, transparent pricing. Start free, scale as you grow.",
                ),
            publishedAt = LocalDateTime.now(),
        )
    }

    private fun createContactPage(tenantId: String): Page {
        val sections =
            listOf(
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.HEADER,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Get in Touch", "level" to 1),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "3rem",
                                        fontWeight = "bold",
                                        margin = Spacing(bottom = "20px"),
                                    ),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Have questions? We're here to help.",
                                    ),
                                styling =
                                    BlockStyling(
                                        textAlign = TextAlign.CENTER,
                                        fontSize = "1.25rem",
                                        color = "#6B7280",
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(top = "80px", bottom = "60px")),
                ),
                ContentSection(
                    id = UUID.randomUUID().toString(),
                    type = SectionType.CONTENT,
                    blocks =
                        listOf(
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Contact Information", "level" to 2),
                                styling = BlockStyling(margin = Spacing(bottom = "20px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content = mapOf("text" to "**Email:** hello@notrayservice.com"),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content = mapOf("text" to "**Sales:** sales@notrayservice.com"),
                                styling = BlockStyling(margin = Spacing(bottom = "15px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content = mapOf("text" to "**Support:** support@notrayservice.com"),
                                styling = BlockStyling(margin = Spacing(bottom = "30px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Office", "level" to 2),
                                styling = BlockStyling(margin = Spacing(top = "40px", bottom = "20px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Notray Service Inc.\n123 Tech Street\nSan Francisco, CA 94105\nUnited States",
                                    ),
                                styling = BlockStyling(margin = Spacing(bottom = "30px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.HEADING,
                                content = mapOf("text" to "Hours", "level" to 2),
                                styling = BlockStyling(margin = Spacing(top = "40px", bottom = "20px")),
                            ),
                            ContentBlock(
                                id = UUID.randomUUID().toString(),
                                type = BlockType.PARAGRAPH,
                                content =
                                    mapOf(
                                        "text" to "Monday - Friday: 9:00 AM - 6:00 PM PST\nSupport available 24/7 for Enterprise customers",
                                    ),
                            ),
                        ),
                    settings = SectionSettings(padding = Spacing(bottom = "80px")),
                ),
            )

        return Page(
            tenantId = tenantId,
            slug = "contact",
            title = "Contact Us - Notray Service",
            metaDescription = "Get in touch with Notray Service. Email, phone, and office location. We're here to help.",
            metaKeywords = "contact, support, sales, email, office, help",
            status = PageStatus.PUBLISHED,
            content = PageContent(sections = sections),
            seoSettings =
                SeoSettings(
                    ogTitle = "Contact Us - Notray Service",
                    ogDescription = "Get in touch with our team",
                ),
            publishedAt = LocalDateTime.now(),
        )
    }
}
