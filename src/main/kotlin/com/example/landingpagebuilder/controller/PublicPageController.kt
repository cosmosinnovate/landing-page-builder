package com.example.landingpagebuilder.controller

import com.example.landingpagebuilder.service.HtmlRenderService
import com.example.landingpagebuilder.service.PageService
import com.example.landingpagebuilder.service.TenantService
import io.swagger.v3.oas.annotations.Hidden
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/public")
@Hidden // Hide from Swagger documentation as these are public-facing pages
class PublicPageController(
    private val pageService: PageService,
    private val tenantService: TenantService,
    private val htmlRenderService: HtmlRenderService,
) {
    /**
     * Serve homepage for a tenant (handles root path and common homepage slugs)
     */
    @GetMapping("/sites/{subdomain}")
    suspend fun getHomepage(
        @PathVariable subdomain: String,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val tenant = tenantService.findBySubdomain(subdomain)

        // Try to find a homepage (home, index, or first available)
        val page =
            pageService.findHomepage(tenant.id!!)
                ?: return createNotFoundResponse("Homepage not found for site: $subdomain")

        val html = htmlRenderService.renderPage(page, tenant)

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .cacheControl(CacheControl.maxAge(Duration.ofMinutes(15)))
            .header(HttpHeaders.VARY, "Accept-Encoding")
            .body(html)
    }

    /**
     * Serve specific page by slug for a tenant
     */
    @GetMapping("/sites/{subdomain}/{slug}")
    suspend fun getPage(
        @PathVariable subdomain: String,
        @PathVariable slug: String,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        try {
            val tenant = tenantService.findBySubdomain(subdomain)
            val page = pageService.findPublishedPage(tenant.id!!, slug)

            val html = htmlRenderService.renderPage(page, tenant)

            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(15)))
                .header(HttpHeaders.VARY, "Accept-Encoding")
                .header("X-Page-ID", page.id)
                .header("X-Tenant-ID", tenant.id)
                .body(html)
        } catch (e: Exception) {
            return createNotFoundResponse("Page not found: $slug")
        }
    }

    /**
     * Handle nested paths (for future use with categories or complex routing)
     */
    @GetMapping("/sites/{subdomain}/{path1}/{path2}")
    suspend fun getNestedPage(
        @PathVariable subdomain: String,
        @PathVariable path1: String,
        @PathVariable path2: String,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        // For now, try to find page with combined slug
        val combinedSlug = "$path1-$path2"
        return try {
            val tenant = tenantService.findBySubdomain(subdomain)
            val page = pageService.findPublishedPage(tenant.id!!, combinedSlug)

            val html = htmlRenderService.renderPage(page, tenant)

            ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(15)))
                .header(HttpHeaders.VARY, "Accept-Encoding")
                .body(html)
        } catch (e: Exception) {
            createNotFoundResponse("Page not found: $combinedSlug")
        }
    }

    /**
     * Handle sitemap.xml requests
     */
    @GetMapping("/sites/{subdomain}/sitemap.xml")
    suspend fun getSitemap(
        @PathVariable subdomain: String,
    ): ResponseEntity<String> {
        try {
            val tenant = tenantService.findBySubdomain(subdomain)
            val publishedPages =
                pageService.findPagesByTenantAndStatus(
                    tenant.id!!,
                    com.example.landingpagebuilder.domain.model.PageStatus.PUBLISHED,
                )

            val baseUrl = getBaseUrl(tenant)
            val sitemap = generateSitemap(publishedPages, baseUrl)

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .cacheControl(CacheControl.maxAge(Duration.ofHours(24)))
                .body(sitemap)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    /**
     * Handle robots.txt requests
     */
    @GetMapping("/sites/{subdomain}/robots.txt")
    suspend fun getRobotsTxt(
        @PathVariable subdomain: String,
    ): ResponseEntity<String> {
        try {
            val tenant = tenantService.findBySubdomain(subdomain)
            val baseUrl = getBaseUrl(tenant)

            val robotsTxt =
                buildString {
                    appendLine("User-agent: *")
                    appendLine("Allow: /")
                    appendLine("")
                    appendLine("Sitemap: $baseUrl/sitemap.xml")
                }

            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
                .body(robotsTxt)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    /**
     * Create a proper 404 response with HTML
     */
    private fun createNotFoundResponse(message: String): ResponseEntity<String> {
        val html =
            """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Page Not Found</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        text-align: center;
                        padding: 50px;
                        background-color: #f8f9fa;
                    }
                    .error-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: white;
                        padding: 40px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    h1 {
                        color: #dc3545;
                        font-size: 3em;
                        margin-bottom: 0.5em;
                    }
                    p {
                        color: #6c757d;
                        font-size: 1.1em;
                        line-height: 1.6;
                    }
                </style>
            </head>
            <body>
                <div class="error-container">
                    <h1>404</h1>
                    <p>$message</p>
                    <p>The page you're looking for might have been moved, deleted, or doesn't exist.</p>
                </div>
            </body>
            </html>
            """.trimIndent()

        return ResponseEntity.status(404)
            .contentType(MediaType.TEXT_HTML)
            .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)))
            .body(html)
    }

    /**
     * Generate sitemap XML
     */
    private fun generateSitemap(
        pages: List<com.example.landingpagebuilder.domain.model.Page>,
        baseUrl: String,
    ): String =
        buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            appendLine("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")

            pages.forEach { page ->
                appendLine("    <url>")
                val pageUrl = if (page.slug.isEmpty()) baseUrl else "$baseUrl/${page.slug}"
                appendLine("        <loc>$pageUrl</loc>")
                page.publishedAt?.let {
                    appendLine("        <lastmod>${it.toLocalDate()}</lastmod>")
                }
                appendLine("        <changefreq>weekly</changefreq>")
                appendLine("        <priority>0.8</priority>")
                appendLine("    </url>")
            }

            appendLine("</urlset>")
        }

    /**
     * Get base URL for tenant
     */
    private fun getBaseUrl(tenant: com.example.landingpagebuilder.domain.model.Tenant): String =
        tenant.settings.customDomain?.let { "https://$it" }
            ?: "https://${tenant.subdomain}.example.com"
}
