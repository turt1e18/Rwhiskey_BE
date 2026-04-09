package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val uid:Int?,
    val email:String?,
    val name:String?
)
