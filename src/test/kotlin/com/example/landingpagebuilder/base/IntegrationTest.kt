package com.example.landingpagebuilder.base

import com.example.landingpagebuilder.config.TestContainersConfig
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@Import(TestContainersConfig::class)
@ActiveProfiles("test")
abstract class IntegrationTest
