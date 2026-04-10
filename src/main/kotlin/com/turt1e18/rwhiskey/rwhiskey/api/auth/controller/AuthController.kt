package com.turt1e18.rwhiskey.rwhiskey.api.auth.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.LoginRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.SignupRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.LoginResponse
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.MeResponse
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.SignupResponse
import com.turt1e18.rwhiskey.rwhiskey.api.auth.service.AuthService
import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        session: HttpSession
    ): ResponseEntity<LoginResponse> {
        val response = authService.login(request,session)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(
        session: HttpSession
    ): ResponseEntity<Void>{
        authService.logout(session)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/me")
    fun me(
        authentication: Authentication?
    ): ResponseEntity<MeResponse>{
        val response = authService.me(authentication)
        return ResponseEntity.ok(response)
    }
}