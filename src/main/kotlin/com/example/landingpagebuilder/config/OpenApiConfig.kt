package com.example.landingpagebuilder.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
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
                Currently, this API does not require authentication.
                In production, you should implement proper authentication and authorization.

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
