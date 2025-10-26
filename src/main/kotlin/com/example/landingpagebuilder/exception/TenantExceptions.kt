package com.example.landingpagebuilder.exception

/**
 * Base exception for all tenant-related errors
 */
abstract class TenantException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Thrown when a tenant is not found
 */
class TenantNotFoundException : TenantException {
    constructor(message: String, cause: Throwable? = null) : super(message, cause)
    constructor(tenantId: String) : super("Tenant not found with ID: $tenantId")
    constructor(subdomain: String, bySubdomain: Boolean) : super("Tenant not found with subdomain: $subdomain")
}

/**
 * Thrown when trying to create a tenant with an existing subdomain
 */
class TenantAlreadyExistsException : TenantException {
    constructor(message: String, cause: Throwable? = null) : super(message, cause)
    constructor(subdomain: String) : super("Tenant already exists with subdomain: $subdomain")
}

/**
 * Thrown when tenant validation fails
 */
class TenantValidationException(
    message: String,
    cause: Throwable? = null,
) : TenantException(message, cause)

/**
 * Thrown when tenant operation is not allowed due to status
 */
class TenantOperationNotAllowedException(
    message: String,
    cause: Throwable? = null,
) : TenantException(message, cause)

/**
 * Thrown when tenant has reached resource limits
 */
class TenantResourceLimitException(
    message: String,
    cause: Throwable? = null,
) : TenantException(message, cause)
