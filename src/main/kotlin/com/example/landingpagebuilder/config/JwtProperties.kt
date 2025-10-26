package com.example.landingpagebuilder.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "",
    var accessTokenExpiration: Long = 3600000,
    var refreshTokenExpiration: Long = 86400000,
)
