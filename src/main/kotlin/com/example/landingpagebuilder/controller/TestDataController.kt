package com.example.landingpagebuilder.controller

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
import com.example.landingpagebuilder.domain.model.TextAlign
import com.example.landingpagebuilder.service.PageService
import com.example.landingpagebuilder.service.TenantService
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/test")
@Hidden
class TestDataController(
    private val pageService: PageService,
    private val tenantService: TenantService,
) {
    @PostMapping("/create-sample-page/{subdomain}")
    suspend fun createSamplePage(
        @PathVariable subdomain: String,
    ): ResponseEntity<String> {
        try {
            val tenant = tenantService.findBySubdomain(subdomain)

            val samplePage =
                Page(
                    tenantId = tenant.id!!,
                    slug = "home",
                    title = "Welcome to $subdomain",
                    metaDescription = "This is a demo landing page built with our page builder system",
                    metaKeywords = "demo, landing page, kotlin, spring boot",
                    status = PageStatus.PUBLISHED,
                    publishedAt = LocalDateTime.now(),
                    content =
                        PageContent(
                            sections =
                                listOf(
                                    // Hero Section
                                    ContentSection(
                                        id = "hero-section",
                                        type = SectionType.HERO,
                                        blocks =
                                            listOf(
                                                ContentBlock(
                                                    id = "hero-heading",
                                                    type = BlockType.HEADING,
                                                    content =
                                                        mapOf(
                                                            "text" to "Welcome to Our Amazing Demo Site",
                                                            "level" to 1,
                                                        ),
                                                    styling =
                                                        BlockStyling(
                                                            textAlign = TextAlign.CENTER,
                                                            fontSize = "3rem",
                                                            fontWeight = "bold",
                                                            color = "#2c3e50",
                                                            margin =
                                                                Spacing(
                                                                    top = "40px",
                                                                    bottom = "20px",
                                                                ),
                                                        ),
                                                ),
                                                ContentBlock(
                                                    id = "hero-paragraph",
                                                    type = BlockType.PARAGRAPH,
                                                    content =
                                                        mapOf(
                                                            "text" to "This is a demonstration of our powerful landing page builder. " +
                                                                "Built with Kotlin, Spring Boot, and MongoDB.",
                                                        ),
                                                    styling =
                                                        BlockStyling(
                                                            textAlign = TextAlign.CENTER,
                                                            fontSize = "1.2rem",
                                                            color = "#7f8c8d",
                                                            margin =
                                                                Spacing(
                                                                    bottom = "30px",
                                                                ),
                                                        ),
                                                ),
                                                ContentBlock(
                                                    id = "hero-button",
                                                    type = BlockType.BUTTON,
                                                    content =
                                                        mapOf(
                                                            "text" to "Get Started",
                                                            "href" to "#features",
                                                            "newTab" to false,
                                                        ),
                                                    styling =
                                                        BlockStyling(
                                                            textAlign = TextAlign.CENTER,
                                                            backgroundColor = "#3498db",
                                                            color = "white",
                                                            padding =
                                                                Spacing(
                                                                    top = "15px",
                                                                    right = "30px",
                                                                    bottom = "15px",
                                                                    left = "30px",
                                                                ),
                                                            borderRadius = "8px",
                                                            fontSize = "1.1rem",
                                                            fontWeight = "bold",
                                                        ),
                                                ),
                                            ),
                                        settings =
                                            SectionSettings(
                                                backgroundColor = "#ecf0f1",
                                                padding =
                                                    Spacing(
                                                        top = "80px",
                                                        bottom = "80px",
                                                    ),
                                            ),
                                    ),
                                    // Features Section
                                    ContentSection(
                                        id = "features-section",
                                        type = SectionType.FEATURES,
                                        blocks =
                                            listOf(
                                                ContentBlock(
                                                    id = "features-heading",
                                                    type = BlockType.HEADING,
                                                    content =
                                                        mapOf(
                                                            "text" to "Key Features",
                                                            "level" to 2,
                                                        ),
                                                    styling =
                                                        BlockStyling(
                                                            textAlign = TextAlign.CENTER,
                                                            fontSize = "2.5rem",
                                                            color = "#2c3e50",
                                                            margin =
                                                                Spacing(
                                                                    bottom = "40px",
                                                                ),
                                                        ),
                                                ),
                                                ContentBlock(
                                                    id = "feature-list",
                                                    type = BlockType.LIST,
                                                    content =
                                                        mapOf(
                                                            "items" to
                                                                listOf(
                                                                    "Kotlin coroutines for non-blocking operations",
                                                                    "MongoDB with advanced indexing",
                                                                    "Complete SEO meta tag support",
                                                                    "Responsive HTML generation",
                                                                    "Flexible content block system",
                                                                ),
                                                            "ordered" to false,
                                                        ),
                                                    styling =
                                                        BlockStyling(
                                                            fontSize = "1.1rem",
                                                            margin =
                                                                Spacing(
                                                                    bottom = "20px",
                                                                ),
                                                        ),
                                                ),
                                                ContentBlock(
                                                    id = "divider",
                                                    type = BlockType.DIVIDER,
                                                    content =
                                                        mapOf(
                                                            "color" to "#bdc3c7",
                                                            "thickness" to "2px",
                                                        ),
                                                ),
                                            ),
                                        settings =
                                            SectionSettings(
                                                padding =
                                                    Spacing(
                                                        top = "60px",
                                                        bottom = "60px",
                                                    ),
                                            ),
                                    ),
                                ),
                            designSettings =
                                DesignSettings(
                                    theme = "modern",
                                    primaryColor = "#3498db",
                                    secondaryColor = "#2c3e50",
                                    fontFamily = "Arial, sans-serif",
                                    containerWidth = "1200px",
                                ),
                        ),
                    seoSettings =
                        SeoSettings(
                            ogTitle = "$subdomain - Amazing Landing Pages",
                            ogDescription = "Experience our powerful landing page builder with this demo site",
                            ogImage = "https://$subdomain.example.com/og-image.jpg",
                            twitterCard = "summary_large_image",
                            noIndex = false,
                            noFollow = false,
                        ),
                )

            val createdPage = pageService.createPage(samplePage)

            return ResponseEntity.ok(
                "Sample page created! Visit: http://localhost:8082/public/sites/$subdomain\nPage ID: ${createdPage.id}",
            )
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Error creating sample page: ${e.message}")
        }
    }
}
