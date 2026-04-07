package com.turt1e18.rwhiskey.rwhiskey.api.health

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }
}