package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class RecommendationSaveRequest(
    @field:JsonProperty("weather_value")
    val weatherValue: String?,
    @field:JsonProperty("mood_value")
    val moodValue: String?,
    @field:JsonProperty("abv_value")
    val abvValue: String?,
    @field:JsonProperty("additional_value")
    val additionalValue: String?,
    @field:JsonProperty("flex_flag")
    val flexFlag: Boolean = false,
    
    // AI 추천 결과 필드 (Flattened)
    // whisky_name과 whiskey_name 모두 허용
    @field:JsonProperty("whisky_name")
    @field:JsonAlias("whiskey_name", "whiskyName")
    val whiskyName: String,

    @field:JsonProperty("whisky_name_en")
    @field:JsonAlias("whiskey_name_en", "whiskyNameEn")
    val whiskyNameEn: String?,

    @field:JsonProperty("classification")
    val classification: String?,

    @field:JsonProperty("region_name")
    val regionName: String?,

    @field:JsonProperty("style_name")
    val styleName: String?,

    @field:JsonProperty("feature_tags")
    val featureTags: List<String>?,

    @field:JsonProperty("food_name")
    val foodName: String?,

    @field:JsonProperty("pairing_note")
    val pairingNote: String?,

    @field:JsonProperty("bartender_word")
    val bartenderWord: String?
)
