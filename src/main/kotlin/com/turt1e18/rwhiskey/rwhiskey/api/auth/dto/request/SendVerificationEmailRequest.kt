package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendVerificationEmailRequest (
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Must be a valid email address")
    val email: String
)