package com.turt1e18.rwhiskey.rwhiskey.api.auth.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.SendVerificationEmailRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.VerifyEmailCodeRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.EmailVerificationResponse
import com.turt1e18.rwhiskey.rwhiskey.api.auth.service.EmailVerificationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth/email")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService
) {
    @PostMapping("/send")
    fun sendVerificationEmail(
        @Valid @RequestBody
        request: SendVerificationEmailRequest): ResponseEntity<EmailVerificationResponse> {
        val response = emailVerificationService.sendVerificationEmail(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify")
    fun verifyEmailCode(
        @Valid @RequestBody
        request: VerifyEmailCodeRequest
    ): ResponseEntity<EmailVerificationResponse> {
        val response = emailVerificationService.verifyEmailCode(
            email = request.email,
            inputCode = request.emailCode
        )
        return ResponseEntity.ok(response)
    }
}