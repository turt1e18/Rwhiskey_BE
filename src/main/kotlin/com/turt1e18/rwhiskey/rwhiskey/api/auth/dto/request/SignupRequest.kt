package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest (
    @field:NotBlank(message = "email is required")
    @field:Email(message = "not collect email")
    val email : String,

    @field:NotBlank(message = "password cannot be blank")
    @field:Size(min = 8, max = 32, message = "Message cannot be blank")
    val password : String,

    @field:NotBlank(message = "name cannot be blank")
    @field:Size(min = 2, max = 255, message = "Message cannot be blank")
    val name : String
)