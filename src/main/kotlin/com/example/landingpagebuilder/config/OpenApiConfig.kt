package com.example.landingpagebuilder.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Value("\${server.port:8082}")
    private val serverPort: Int = 8082

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(apiInfo())
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:$serverPort")
                        .description("Local Development Server"),
                    Server()
                        .url("https://api.example.com")
                        .description("Production Server"),
                ),
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "Bearer Authentication",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter your JWT token obtained from /api/v1/auth/login or /api/v1/auth/signup"),
                    ),
            )

    private fun apiInfo(): Info =
        Info()
            .title("Landing Page Builder API")
            .description(
                """
                Multi-tenant Landing Page Builder REST API.

                ## Features
                - Multi-tenant architecture with subdomain routing
                - Landing page creation and management
                - Custom domain support
                - SEO optimization
                - HTML rendering and publishing

                ## Authentication
                This API uses JWT (JSON Web Token) Bearer authentication.
                
                ### How to authenticate:
                1. **Sign up**: POST to `/api/v1/auth/signup` with your credentials
                2. **Login**: POST to `/api/v1/auth/login` to get your access token
                3. **Use Token**: Click the 🔓 Authorize button and enter: your token
                4. All protected endpoints will now include your JWT token automatically
                
                ### Token Format
                The token should be entered as-is (without "Bearer " prefix) in the Authorize dialog.

                ## Multi-tenant Access
                - **Admin API**: Use `/api/v1/tenants/*` endpoints to manage tenants
                - **Public Pages**: Access published pages via subdomain routing

                ## Rate Limiting
                No rate limiting is currently implemented.
                """.trimIndent(),
            )
            .version("1.0.0")
            .contact(
                Contact()
                    .name("Landing Page Builder Team")
                    .email("support@example.com")
                    .url("https://github.com/example/landing-page-builder"),
            )
            .license(
                License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"),
            )
}
