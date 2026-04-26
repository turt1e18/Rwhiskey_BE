package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request

data class RecommendationResultRequestDto(
    val whiskeyName: String,
    val whiskeyCategory: Int?,
    val whiskeyFlavor: String?, // JSON string
    val whiskeyRegion: Int?,
    val whiskeyStyle: Int?,
    val snack: String?,
    val reason: String?,
    val bartender: String?
)
