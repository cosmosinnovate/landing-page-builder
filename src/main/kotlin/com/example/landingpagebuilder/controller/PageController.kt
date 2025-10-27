package com.example.landingpagebuilder.controller

import com.example.landingpagebuilder.domain.model.Page
import com.example.landingpagebuilder.domain.model.PageStatus
import com.example.landingpagebuilder.dto.CreatePageRequest
import com.example.landingpagebuilder.dto.UpdatePageRequest
import com.example.landingpagebuilder.service.PageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pages")
@Tag(name = "Page Management", description = "APIs for managing landing pages")
@SecurityRequirement(name = "Bearer Authentication")
class PageController(
    private val pageService: PageService,
) {
    @Operation(
        summary = "Get all pages for a tenant",
        description = "Retrieves all pages belonging to a specific tenant",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Pages retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Tenant not found"),
        ],
    )
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun getPagesByTenant(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
    ): ResponseEntity<List<Page>> {
        val pages = pageService.findPagesByTenant(tenantId)
        return ResponseEntity.ok(pages)
    }

    @Operation(
        summary = "Get pages by tenant and status",
        description = "Retrieves pages belonging to a tenant with a specific status",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Pages retrieved successfully"),
        ],
    )
    @GetMapping("/tenant/{tenantId}/status/{status}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun getPagesByTenantAndStatus(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
        @Parameter(description = "Page status") @PathVariable status: PageStatus,
    ): ResponseEntity<List<Page>> {
        val pages = pageService.findPagesByTenantAndStatus(tenantId, status)
        return ResponseEntity.ok(pages)
    }

    @Operation(summary = "Get page by ID", description = "Retrieves a specific page by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page found"),
            ApiResponse(responseCode = "404", description = "Page not found"),
        ],
    )
    @GetMapping("/{pageId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    suspend fun getPageById(
        @Parameter(description = "Page ID") @PathVariable pageId: String,
    ): ResponseEntity<Page> {
        val page = pageService.findById(pageId)
        return ResponseEntity.ok(page)
    }

    @Operation(summary = "Create a new page", description = "Creates a new landing page")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Page created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "409", description = "Slug already exists"),
        ],
    )
    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun createPage(
        @Valid @RequestBody request: CreatePageRequest,
    ): ResponseEntity<Page> {
        val page =
            Page(
                tenantId = request.tenantId,
                slug = request.slug,
                title = request.title,
                metaDescription = request.metaDescription,
                metaKeywords = request.metaKeywords,
                status = request.status,
                content = request.content,
                seoSettings = request.seoSettings,
            )
        val createdPage = pageService.createPage(page)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPage)
    }

    @Operation(summary = "Update a page", description = "Updates an existing page")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page updated successfully"),
            ApiResponse(responseCode = "404", description = "Page not found"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "409", description = "Slug conflict"),
        ],
    )
    @PutMapping("/{pageId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun updatePage(
        @Parameter(description = "Page ID") @PathVariable pageId: String,
        @Valid @RequestBody request: UpdatePageRequest,
    ): ResponseEntity<Page> {
        val existingPage = pageService.findById(pageId)
        val updatedPage =
            existingPage.copy(
                slug = request.slug,
                title = request.title,
                metaDescription = request.metaDescription,
                metaKeywords = request.metaKeywords,
                status = request.status,
                content = request.content,
                seoSettings = request.seoSettings,
            )
        val savedPage = pageService.updatePage(pageId, updatedPage)
        return ResponseEntity.ok(savedPage)
    }

    @Operation(summary = "Delete a page", description = "Deletes a page by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Page deleted successfully"),
            ApiResponse(responseCode = "404", description = "Page not found"),
        ],
    )
    @DeleteMapping("/{pageId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    suspend fun deletePage(
        @Parameter(description = "Page ID") @PathVariable pageId: String,
    ): ResponseEntity<Void> {
        pageService.deletePage(pageId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Publish a page", description = "Publishes a draft page")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page published successfully"),
            ApiResponse(responseCode = "404", description = "Page not found"),
        ],
    )
    @PatchMapping("/{pageId}/publish")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun publishPage(
        @Parameter(description = "Page ID") @PathVariable pageId: String,
    ): ResponseEntity<Page> {
        val publishedPage = pageService.publishPage(pageId)
        return ResponseEntity.ok(publishedPage)
    }

    @Operation(summary = "Unpublish a page", description = "Unpublishes a page (sets to draft)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page unpublished successfully"),
            ApiResponse(responseCode = "404", description = "Page not found"),
        ],
    )
    @PatchMapping("/{pageId}/unpublish")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun unpublishPage(
        @Parameter(description = "Page ID") @PathVariable pageId: String,
    ): ResponseEntity<Page> {
        val unpublishedPage = pageService.unpublishPage(pageId)
        return ResponseEntity.ok(unpublishedPage)
    }

    @Operation(summary = "Check slug availability", description = "Checks if a slug is available for a tenant")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Availability check completed"),
        ],
    )
    @GetMapping("/tenant/{tenantId}/slug/{slug}/availability")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    suspend fun checkSlugAvailability(
        @Parameter(description = "Tenant ID") @PathVariable tenantId: String,
        @Parameter(description = "Slug to check") @PathVariable slug: String,
    ): ResponseEntity<Map<String, Boolean>> {
        val isAvailable = pageService.isSlugAvailable(tenantId, slug)
        return ResponseEntity.ok(mapOf("available" to isAvailable))
    }
}

