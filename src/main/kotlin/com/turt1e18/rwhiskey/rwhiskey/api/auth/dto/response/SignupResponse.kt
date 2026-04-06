package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response

data class SignupResponse (
    val uid: Int,
    val email: String,
    val name: String,
    val message: String
)