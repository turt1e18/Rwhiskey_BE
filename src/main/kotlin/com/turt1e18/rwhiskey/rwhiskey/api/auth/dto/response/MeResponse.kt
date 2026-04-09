package com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response

data class MeResponse(
    val authenticated: Boolean,
    val uid: Int?,
    val email: String?,
    val name: String?,
)
