package com.example.landingpagebuilder.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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

        if (jwtTokenService.validateToken(token) && !jwtTokenService.isTokenExpired(token)) {
            val userId = jwtTokenService.getUserIdFromToken(token)
            val email = jwtTokenService.getEmailFromToken(token)
            val role = jwtTokenService.getRoleFromToken(token)

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
            }
        }

        filterChain.doFilter(request, response)
    }
}
