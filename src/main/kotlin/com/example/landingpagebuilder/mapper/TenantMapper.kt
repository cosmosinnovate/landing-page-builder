package com.example.landingpagebuilder.mapper

import com.example.landingpagebuilder.domain.model.Tenant
import com.example.landingpagebuilder.domain.model.TenantSettings
import com.example.landingpagebuilder.dto.CreateTenantRequest
import com.example.landingpagebuilder.dto.TenantResponse
import com.example.landingpagebuilder.dto.TenantSettingsDto
import com.example.landingpagebuilder.dto.UpdateTenantRequest

object TenantMapper {
    fun toEntity(request: CreateTenantRequest): Tenant =
        Tenant(
            subdomain = request.subdomain,
            name = request.name,
            email = request.email,
            settings = request.settings?.toEntity() ?: TenantSettings(),
        )

    fun toEntity(
        existing: Tenant,
        request: UpdateTenantRequest,
    ): Tenant =
        existing.copy(
            subdomain = request.subdomain ?: existing.subdomain,
            name = request.name ?: existing.name,
            email = request.email ?: existing.email,
            settings = request.settings?.toEntity() ?: existing.settings,
        )

    fun toResponse(tenant: Tenant): TenantResponse =
        TenantResponse(
            id = tenant.id ?: "",
            subdomain = tenant.subdomain,
            name = tenant.name,
            email = tenant.email,
            status = tenant.status,
            settings = tenant.settings.toDto(),
            createdAt = tenant.createdAt,
            updatedAt = tenant.updatedAt,
        )

    fun toResponseList(tenants: List<Tenant>): List<TenantResponse> = tenants.map { toResponse(it) }

    private fun TenantSettingsDto.toEntity(): TenantSettings =
        TenantSettings(
            customDomain = this.customDomain,
            logoUrl = this.logoUrl,
            primaryColor = this.primaryColor,
            secondaryColor = this.secondaryColor,
            allowCustomStyling = this.allowCustomStyling,
            maxPages = this.maxPages,
            storageLimit = this.storageLimit,
            features = this.features,
        )

    private fun TenantSettings.toDto(): TenantSettingsDto =
        TenantSettingsDto(
            customDomain = this.customDomain,
            logoUrl = this.logoUrl,
            primaryColor = this.primaryColor,
            secondaryColor = this.secondaryColor,
            allowCustomStyling = this.allowCustomStyling,
            maxPages = this.maxPages,
            storageLimit = this.storageLimit,
            features = this.features,
        )
}
