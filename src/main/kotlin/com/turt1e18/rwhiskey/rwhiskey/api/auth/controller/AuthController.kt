package com.turt1e18.rwhiskey.rwhiskey.api.auth.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.SignupRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.SignupResponse
import com.turt1e18.rwhiskey.rwhiskey.api.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody signupRequest: SignupRequest
    ): ResponseEntity<SignupResponse> {
        val response = authService.signUp(signupRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}