package com.example.landingpagebuilder.domain.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
@CompoundIndex(
    name = "user_tenant_status_idx",
    def = "{'tenantId': 1, 'status': 1, 'createdAt': -1}",
)
data class User(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val tenantId: String,
    val role: UserRole = UserRole.OWNER,
    val status: UserStatus = UserStatus.ACTIVE,
    val emailVerified: Boolean = false,
    val lastLoginAt: LocalDateTime? = null,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    val fullName: String
        get() = "$firstName $lastName"
}

enum class UserRole {
    OWNER,
    ADMIN,
    EDITOR,
    VIEWER,
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION,
}
