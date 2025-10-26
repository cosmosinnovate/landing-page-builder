package com.example.landingpagebuilder.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/")
    fun hello(): String {
        return "Hello World! Landing Page Builder is up and running!"
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Landing Page Builder",
            "version" to "0.0.1",
        )
    }
}
