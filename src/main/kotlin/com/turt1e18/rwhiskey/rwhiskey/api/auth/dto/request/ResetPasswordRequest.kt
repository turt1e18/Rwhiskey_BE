package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResetPasswordRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    val newPassword: String
)
