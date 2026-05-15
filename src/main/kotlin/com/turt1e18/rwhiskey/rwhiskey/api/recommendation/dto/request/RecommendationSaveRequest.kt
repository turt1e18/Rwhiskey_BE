package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class RecommendationSaveRequest(
    val weatherValue: String?,
    val moodValue: String?,
    val abvValue: String?,
    val additionalValue: String?,
    val flexFlag: Boolean = false,
    val mainTag: String? = null,
    
    // AI 추천 결과 필드 (Flattened)
    // whiskeyName과 whiskyName 모두 허용
    @field:JsonAlias("whisky_name", "whiskey_name", "whiskyName")
    val whiskeyName: String,

    @field:JsonAlias("whisky_name_en", "whiskey_name_en", "whiskeyNameEn")
    val whiskeyNameEn: String?,

    @field:JsonAlias("classification")
    val whiskeyCategory: String?,
    val regionName: String?,
    val styleName: String?,
    val featureTags: List<String>?,
    val foodName: String?,
    val pairingNote: String?,
    val bartenderWord: String?
)
