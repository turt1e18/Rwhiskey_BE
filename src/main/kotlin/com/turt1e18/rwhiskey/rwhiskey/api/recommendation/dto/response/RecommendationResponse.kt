package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.response

import java.time.LocalDateTime

data class RecommendationResponse(
    val oid: Int,
    val uid: Int,
    val orderDate: LocalDateTime,
    val whiskeyName: String
)
