package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerifyEmailCodeRequest (
    @field:NotBlank(message = "EmailCode cannot be blank")
    @field:Pattern(
        regexp = "^[0-9]{6}$",
        message = "인증 코드는 6자리 숫자여야 합니다."
    )
    val emailCode: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Must be a valid email address")
    val email : String
)