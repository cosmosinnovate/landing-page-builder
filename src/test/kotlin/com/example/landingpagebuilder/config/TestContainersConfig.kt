package com.example.landingpagebuilder.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfig {
    @Bean
    @ServiceConnection
    fun mongoDBContainer(): MongoDBContainer =
        MongoDBContainer(DockerImageName.parse("mongo:7.0"))
            .withExposedPorts(27017)
            .withReuse(true)

    @Bean
    @ServiceConnection
    fun redisContainer(): GenericContainer<*> =
        GenericContainer(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379)
            .withReuse(true)
}
