package com.example.landingpagebuilder.repository

import com.example.landingpagebuilder.domain.model.User
import com.example.landingpagebuilder.domain.model.UserRole
import com.example.landingpagebuilder.domain.model.UserStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : MongoRepository<User, String> {
    /**
     * Find user by email (unique constraint)
     */
    fun findByEmail(email: String): Optional<User>

    /**
     * Check if email exists (for validation)
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Find all users by tenant ID
     */
    fun findByTenantId(tenantId: String): List<User>

    /**
     * Find users by tenant ID and status
     */
    @Query("{ 'tenantId': ?0, 'status': ?1 }")
    fun findByTenantIdAndStatus(
        tenantId: String,
        status: UserStatus,
    ): List<User>

    /**
     * Find active users by tenant ID
     */
    @Query("{ 'tenantId': ?0, 'status': 'ACTIVE' }")
    fun findActiveUsersByTenantId(tenantId: String): List<User>

    /**
     * Find users by role
     */
    fun findByRole(role: UserRole): List<User>

    /**
     * Find tenant owner by tenant ID
     */
    @Query("{ 'tenantId': ?0, 'role': 'OWNER' }")
    fun findTenantOwner(tenantId: String): Optional<User>

    /**
     * Count users by tenant ID
     */
    fun countByTenantId(tenantId: String): Long

    /**
     * Count users by status
     */
    fun countByStatus(status: UserStatus): Long
}
