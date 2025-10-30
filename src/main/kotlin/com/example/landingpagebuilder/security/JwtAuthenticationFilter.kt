package com.example.landingpagebuilder.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService,
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        try {
            if (jwtTokenService.validateToken(token) && !jwtTokenService.isTokenExpired(token)) {
                val userId = jwtTokenService.getUserIdFromToken(token)
                val email = jwtTokenService.getEmailFromToken(token)
                val role = jwtTokenService.getRoleFromToken(token)

                logger.debug("JWT Token parsed - userId: $userId, email: $email, role: $role")

                if (userId != null && email != null && role != null) {
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                    val userDetails = User(email, "", authorities)

                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities,
                        )

                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication

                    logger.debug("Authentication set with authorities: ${authorities.map { it.authority }}")
                } else {
                    logger.warn("JWT Token missing required claims - userId: $userId, email: $email, role: $role")
                }
            } else {
                logger.warn("JWT Token validation failed or expired")
            }
        } catch (e: Exception) {
            logger.error("Error processing JWT token: ${e.message}", e)
        }

        filterChain.doFilter(request, response)
    }
}
