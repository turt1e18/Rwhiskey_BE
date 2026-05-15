package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class RecommendationDetailResponse(
    val oid: Int,
    val orderDate: LocalDateTime,

    // 요청 정보
    val weatherValue: String?,
    val moodValue: String?,
    val abvValue: String?,
    val additionalValue: String?,
    val flexFlag: Boolean,
    val mainTag: String?,

    // 추천 결과 정보
    val whiskeyName: String,
    val whiskeyNameEn: String?,
    val whiskeyCategory: String?,
    val featureTags: List<String>?,
    val foodName: String?,
    val pairingNote: String?,
    val bartenderWord: String?
)
