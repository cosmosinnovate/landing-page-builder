package com.example.landingpagebuilder.service

import com.example.landingpagebuilder.domain.model.TenantFeature
import com.example.landingpagebuilder.domain.model.TenantStatus
import com.example.landingpagebuilder.exception.TenantAlreadyExistsException
import com.example.landingpagebuilder.exception.TenantNotFoundException
import com.example.landingpagebuilder.exception.TenantOperationNotAllowedException
import com.example.landingpagebuilder.exception.TenantValidationException
import com.example.landingpagebuilder.fixture.TestDataFactory
import com.example.landingpagebuilder.repository.TenantRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.Optional

class TenantServiceTest {
    private lateinit var tenantRepository: TenantRepository
    private lateinit var tenantService: TenantService

    @BeforeEach
    fun setUp() {
        tenantRepository = mockk()
        tenantService = TenantService(tenantRepository)
    }

    @Test
    fun `createTenant should create valid tenant`() =
        runTest {
            // Given
            val newTenant =
                TestDataFactory.createTenant(
                    id = null,
                    subdomain = "valid-tenant",
                    email = "test@example.com",
                )
            val savedTenant = newTenant.copy(id = "generated-id")

            every { tenantRepository.existsBySubdomain(newTenant.subdomain) } returns false
            every { tenantRepository.save(any()) } returns savedTenant

            // When
            val result = tenantService.createTenant(newTenant)

            // Then
            assertNotNull(result.id)
            assertEquals("valid-tenant", result.subdomain)
            coVerify(exactly = 1) { tenantRepository.existsBySubdomain(newTenant.subdomain) }
            coVerify(exactly = 1) { tenantRepository.save(any()) }
        }

    @Test
    fun `createTenant should throw exception when subdomain already exists`() =
        runTest {
            // Given
            val newTenant = TestDataFactory.createTenant(subdomain = "existing-tenant")
            every { tenantRepository.existsBySubdomain(newTenant.subdomain) } returns true

            // When & Then
            assertThrows<TenantAlreadyExistsException> {
                tenantService.createTenant(newTenant)
            }
            coVerify(exactly = 1) { tenantRepository.existsBySubdomain(newTenant.subdomain) }
            coVerify(exactly = 0) { tenantRepository.save(any()) }
        }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", "a", "ab", "INVALID", "invalid_", "_invalid", "invalid--tenant",
            "a-very-long-subdomain-name-that-exceeds-the-maximum-allowed-length-limit",
        ],
    )
    fun `createTenant should throw exception for invalid subdomain format`(invalidSubdomain: String) =
        runTest {
            // Given
            val newTenant = TestDataFactory.createTenant(subdomain = invalidSubdomain)

            // When & Then
            assertThrows<TenantValidationException> {
                tenantService.createTenant(newTenant)
            }
        }

    @ParameterizedTest
    @ValueSource(strings = ["invalid-email", "test@", "@example.com", "test@.com", "test@com"])
    fun `createTenant should throw exception for invalid email format`(invalidEmail: String) =
        runTest {
            // Given
            val newTenant =
                TestDataFactory.createTenant(
                    subdomain = "valid-subdomain",
                    email = invalidEmail,
                )
            every { tenantRepository.existsBySubdomain(newTenant.subdomain) } returns false

            // When & Then
            assertThrows<TenantValidationException> {
                tenantService.createTenant(newTenant)
            }
        }

    @Test
    fun `createTenant should throw exception for blank name`() =
        runTest {
            // Given
            val newTenant =
                TestDataFactory.createTenant(
                    subdomain = "valid-subdomain",
                    name = "   ",
                )
            every { tenantRepository.existsBySubdomain(newTenant.subdomain) } returns false

            // When & Then
            assertThrows<TenantValidationException> {
                tenantService.createTenant(newTenant)
            }
        }

    @Test
    fun `createTenant should throw exception for invalid custom domain`() =
        runTest {
            // Given
            val newTenant =
                TestDataFactory.createTenant(
                    subdomain = "valid-subdomain",
                    settings = TestDataFactory.createTenantSettings(customDomain = "invalid_domain"),
                )
            every { tenantRepository.existsBySubdomain(newTenant.subdomain) } returns false

            // When & Then
            assertThrows<TenantValidationException> {
                tenantService.createTenant(newTenant)
            }
        }

    @Test
    fun `findById should return tenant when exists`() =
        runTest {
            // Given
            val tenant = TestDataFactory.createTenant(id = "tenant-id")
            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)

            // When
            val result = tenantService.findById("tenant-id")

            // Then
            assertEquals("tenant-id", result.id)
            coVerify(exactly = 1) { tenantRepository.findById("tenant-id") }
        }

    @Test
    fun `findById should throw exception when not found`() =
        runTest {
            // Given
            every { tenantRepository.findById("non-existent") } returns Optional.empty()

            // When & Then
            assertThrows<TenantNotFoundException> {
                tenantService.findById("non-existent")
            }
        }

    @Test
    fun `findBySubdomain should return tenant when exists`() =
        runTest {
            // Given
            val tenant = TestDataFactory.createTenant(subdomain = "existing-tenant")
            every { tenantRepository.findBySubdomain("existing-tenant") } returns Optional.of(tenant)

            // When
            val result = tenantService.findBySubdomain("existing-tenant")

            // Then
            assertEquals("existing-tenant", result.subdomain)
            coVerify(exactly = 1) { tenantRepository.findBySubdomain("existing-tenant") }
        }

    @Test
    fun `findBySubdomain should throw exception when not found`() =
        runTest {
            // Given
            every { tenantRepository.findBySubdomain("non-existent") } returns Optional.empty()

            // When & Then
            assertThrows<TenantNotFoundException> {
                tenantService.findBySubdomain("non-existent")
            }
        }

    @Test
    fun `findByCustomDomain should return tenant when exists`() =
        runTest {
            // Given
            val tenant = TestDataFactory.createTenantWithCustomDomain(customDomain = "custom.example.com")
            every { tenantRepository.findByCustomDomain("custom.example.com") } returns Optional.of(tenant)

            // When
            val result = tenantService.findByCustomDomain("custom.example.com")

            // Then
            assertEquals("custom.example.com", result.settings.customDomain)
            coVerify(exactly = 1) { tenantRepository.findByCustomDomain("custom.example.com") }
        }

    @Test
    fun `updateTenant should update existing tenant`() =
        runTest {
            // Given
            val existingTenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    subdomain = "original-subdomain",
                )
            val updates =
                existingTenant.copy(
                    name = "Updated Name",
                    settings =
                        TestDataFactory.createTenantSettings(
                            features = setOf(TenantFeature.ADVANCED_EDITOR, TenantFeature.CUSTOM_CSS),
                        ),
                )

            every { tenantRepository.findById("tenant-id") } returns Optional.of(existingTenant)
            every { tenantRepository.save(any()) } returns updates

            // When
            val result = tenantService.updateTenant("tenant-id", updates)

            // Then
            assertEquals("Updated Name", result.name)
            assertTrue(result.settings.features.contains(TenantFeature.ADVANCED_EDITOR))
            coVerify(exactly = 1) { tenantRepository.save(any()) }
        }

    @Test
    fun `updateTenant should throw exception when changing to existing subdomain`() =
        runTest {
            // Given
            val existingTenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    subdomain = "original-subdomain",
                )
            val updates = existingTenant.copy(subdomain = "taken-subdomain")

            every { tenantRepository.findById("tenant-id") } returns Optional.of(existingTenant)
            every { tenantRepository.existsBySubdomain("taken-subdomain") } returns true

            // When & Then
            assertThrows<TenantAlreadyExistsException> {
                tenantService.updateTenant("tenant-id", updates)
            }
        }

    @Test
    fun `updateTenantStatus should change status`() =
        runTest {
            // Given
            val tenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    status = TenantStatus.ACTIVE,
                )
            val updatedTenant = tenant.copy(status = TenantStatus.SUSPENDED)

            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)
            every { tenantRepository.save(any()) } returns updatedTenant

            // When
            val result = tenantService.updateTenantStatus("tenant-id", TenantStatus.SUSPENDED)

            // Then
            assertEquals(TenantStatus.SUSPENDED, result.status)
        }

    @Test
    fun `activateTenant should set status to ACTIVE`() =
        runTest {
            // Given
            val tenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    status = TenantStatus.PENDING_ACTIVATION,
                )
            val activatedTenant = tenant.copy(status = TenantStatus.ACTIVE)

            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)
            every { tenantRepository.save(any()) } returns activatedTenant

            // When
            val result = tenantService.activateTenant("tenant-id")

            // Then
            assertEquals(TenantStatus.ACTIVE, result.status)
        }

    @Test
    fun `suspendTenant should set status to SUSPENDED`() =
        runTest {
            // Given
            val tenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    status = TenantStatus.ACTIVE,
                )
            val suspendedTenant = tenant.copy(status = TenantStatus.SUSPENDED)

            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)
            every { tenantRepository.save(any()) } returns suspendedTenant

            // When
            val result = tenantService.suspendTenant("tenant-id")

            // Then
            assertEquals(TenantStatus.SUSPENDED, result.status)
        }

    @Test
    fun `suspendTenant should throw exception when already suspended`() =
        runTest {
            // Given
            val tenant = TestDataFactory.createSuspendedTenant().copy(id = "tenant-id")
            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)

            // When & Then
            assertThrows<TenantOperationNotAllowedException> {
                tenantService.suspendTenant("tenant-id")
            }
        }

    @Test
    fun `deleteTenant should set status to INACTIVE`() =
        runTest {
            // Given
            val tenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    status = TenantStatus.ACTIVE,
                )
            val deletedTenant = tenant.copy(status = TenantStatus.INACTIVE)

            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)
            every { tenantRepository.save(any()) } returns deletedTenant

            // When
            tenantService.deleteTenant("tenant-id")

            // Then
            val savedTenantSlot = slot<com.example.landingpagebuilder.domain.model.Tenant>()
            coVerify(exactly = 1) { tenantRepository.save(capture(savedTenantSlot)) }
            assertEquals(TenantStatus.INACTIVE, savedTenantSlot.captured.status)
        }

    @Test
    fun `deleteTenant should throw exception when already inactive`() =
        runTest {
            // Given
            val tenant =
                TestDataFactory.createTenant(
                    id = "tenant-id",
                    status = TenantStatus.INACTIVE,
                )
            every { tenantRepository.findById("tenant-id") } returns Optional.of(tenant)

            // When & Then
            assertThrows<TenantOperationNotAllowedException> {
                tenantService.deleteTenant("tenant-id")
            }
        }

    @Test
    fun `findTenantsByStatus should return filtered tenants`() =
        runTest {
            // Given
            val activeTenants =
                listOf(
                    TestDataFactory.createActiveTenant(subdomain = "tenant1"),
                    TestDataFactory.createActiveTenant(subdomain = "tenant2"),
                )
            every { tenantRepository.findByStatusOrderByCreatedAtDesc(TenantStatus.ACTIVE) } returns activeTenants

            // When
            val result = tenantService.findTenantsByStatus(TenantStatus.ACTIVE)

            // Then
            assertEquals(2, result.size)
            assertTrue(result.all { it.status == TenantStatus.ACTIVE })
        }

    @Test
    fun `findActiveTenants should return only active tenants`() =
        runTest {
            // Given
            val activeTenants =
                listOf(
                    TestDataFactory.createActiveTenant(subdomain = "tenant1"),
                    TestDataFactory.createActiveTenant(subdomain = "tenant2"),
                )
            every { tenantRepository.findActiveTenants() } returns activeTenants

            // When
            val result = tenantService.findActiveTenants()

            // Then
            assertEquals(2, result.size)
            assertTrue(result.all { it.status == TenantStatus.ACTIVE })
        }

    @Test
    fun `countTenantsByStatus should return correct count`() =
        runTest {
            // Given
            every { tenantRepository.countByStatus(TenantStatus.ACTIVE) } returns 5L

            // When
            val result = tenantService.countTenantsByStatus(TenantStatus.ACTIVE)

            // Then
            assertEquals(5L, result)
        }

    @Test
    fun `isSubdomainAvailable should return true when subdomain is available`() =
        runTest {
            // Given
            every { tenantRepository.existsBySubdomain("available-subdomain") } returns false

            // When
            val result = tenantService.isSubdomainAvailable("available-subdomain")

            // Then
            assertTrue(result)
        }

    @Test
    fun `isSubdomainAvailable should return false when subdomain is taken`() =
        runTest {
            // Given
            every { tenantRepository.existsBySubdomain("taken-subdomain") } returns true

            // When
            val result = tenantService.isSubdomainAvailable("taken-subdomain")

            // Then
            assertFalse(result)
        }
}
