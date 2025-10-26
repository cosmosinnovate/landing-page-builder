package com.example.landingpagebuilder.security

import com.example.landingpagebuilder.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user =
            userRepository.findByEmail(email)
                .orElseThrow { UsernameNotFoundException("User not found with email: $email") }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        return User
            .builder()
            .username(user.email)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(user.status == com.example.landingpagebuilder.domain.model.UserStatus.SUSPENDED)
            .credentialsExpired(false)
            .disabled(user.status == com.example.landingpagebuilder.domain.model.UserStatus.INACTIVE)
            .build()
    }
}
