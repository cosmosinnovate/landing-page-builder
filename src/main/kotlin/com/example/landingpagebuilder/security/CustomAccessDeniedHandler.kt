package com.example.landingpagebuilder.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        val authentication = SecurityContextHolder.getContext().authentication

        logger.error(
            "Access denied for request: ${request.method} ${request.requestURI}",
        )
        logger.error("Authentication: ${authentication?.name ?: "none"}")
        logger.error("Authorities: ${authentication?.authorities?.map { it.authority } ?: "none"}")
        logger.error("Error: ${accessDeniedException.message}")

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_FORBIDDEN

        val errorResponse =
            mapOf<String, Any>(
                "error" to "Forbidden",
                "message" to "You do not have permission to access this resource",
                "details" to
                    mapOf<String, Any>(
                        "path" to request.requestURI,
                        "method" to request.method,
                        "user" to (authentication?.name ?: "anonymous"),
                        "authorities" to (authentication?.authorities?.map { it.authority } ?: emptyList<String>()),
                        "requiredRole" to "One of: ROLE_OWNER, ROLE_ADMIN, or ROLE_EDITOR",
                    ),
                "timestamp" to System.currentTimeMillis(),
            )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
