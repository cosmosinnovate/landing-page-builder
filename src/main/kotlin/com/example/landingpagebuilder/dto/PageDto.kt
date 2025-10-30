package com.example.landingpagebuilder.dto

import com.example.landingpagebuilder.domain.model.PageContent
import com.example.landingpagebuilder.domain.model.PageStatus
import com.example.landingpagebuilder.domain.model.SeoSettings
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Request to create a new page")
data class CreatePageRequest(
    @Schema(description = "Tenant ID", required = true)
    @field:NotBlank(message = "Tenant ID is required")
    val tenantId: String,
    @Schema(description = "Page slug (URL-friendly)", example = "about-us", required = true)
    @field:NotBlank(message = "Slug is required")
    val slug: String,
    @Schema(description = "Page title", example = "About Us", required = true)
    @field:NotBlank(message = "Title is required")
    val title: String,
    @Schema(description = "Meta description for SEO")
    val metaDescription: String? = null,
    @Schema(description = "Meta keywords for SEO")
    val metaKeywords: String? = null,
    @Schema(description = "Page status")
    val status: PageStatus = PageStatus.DRAFT,
    @Schema(description = "Page content structure")
    val content: PageContent = PageContent(),
    @Schema(description = "SEO settings")
    val seoSettings: SeoSettings = SeoSettings(),
)

@Schema(description = "Request to update an existing page")
data class UpdatePageRequest(
    @Schema(description = "Page slug (URL-friendly)", example = "about-us", required = true)
    @field:NotBlank(message = "Slug is required")
    val slug: String,
    @Schema(description = "Page title", example = "About Us", required = true)
    @field:NotBlank(message = "Title is required")
    val title: String,
    @Schema(description = "Meta description for SEO")
    val metaDescription: String? = null,
    @Schema(description = "Meta keywords for SEO")
    val metaKeywords: String? = null,
    @Schema(description = "Page status")
    val status: PageStatus = PageStatus.DRAFT,
    @Schema(description = "Page content structure")
    val content: PageContent = PageContent(),
    @Schema(description = "SEO settings")
    val seoSettings: SeoSettings = SeoSettings(),
)
