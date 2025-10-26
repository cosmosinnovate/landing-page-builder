package com.example.landingpagebuilder.domain.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "tenants")
@CompoundIndex(
    name = "tenant_status_idx",
    def = "{'status': 1, 'createdAt': -1}",
)
data class Tenant(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val subdomain: String,
    val name: String,
    val email: String,
    val status: TenantStatus = TenantStatus.ACTIVE,
    val settings: TenantSettings = TenantSettings(),
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

enum class TenantStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_ACTIVATION,
}

data class TenantSettings(
    val customDomain: String? = null,
    val logoUrl: String? = null,
    val primaryColor: String = "#007bff",
    val secondaryColor: String = "#6c757d",
    val allowCustomStyling: Boolean = true,
    val maxPages: Int = 10,
    // 1MB in bytes
    val storageLimit: Long = 1048576L,
    val features: Set<TenantFeature> = setOf(TenantFeature.BASIC_EDITOR),
)

enum class TenantFeature {
    BASIC_EDITOR,
    ADVANCED_EDITOR,
    CUSTOM_CSS,
    ANALYTICS,
    CUSTOM_DOMAIN,
    API_ACCESS,
    TEAM_COLLABORATION,
}
