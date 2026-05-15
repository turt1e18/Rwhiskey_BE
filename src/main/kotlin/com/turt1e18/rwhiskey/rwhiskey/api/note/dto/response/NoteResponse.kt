package com.turt1e18.rwhiskey.rwhiskey.api.note.dto.response

import java.time.LocalDateTime

/**
 * 테이스팅 노트 통합 응답 DTO (Part A + Part B)
 */
data class NoteResponse(
    val id: Int,
    val oid: Int,
    val status: String, // "미평가" | "평가완료"

    // [Part A: Recommendation Context - Immutable]
    val whiskeyName: String,
    val whiskeyNameEn: String?,
    val whiskeyCategory: String?,
    val regionId: Int?,
    val styleId: Int?,
    val featureTags: List<String>?, // JSON (ID List)
    val foodName: String?,
    val additionalValue: String?,
    val pairingNote: String?,
    val bartenderWord: String?,
    val weatherValue: String?,
    val moodValue: String?,
    val abvValue: String?,
    val recommendedAt: LocalDateTime,

    // [Part B: User Review - Mutable]
    val reviewType: String?,
    val rating: Double?,
    val nose: String?,
    val palate: String?,
    val finish: String?,
    val memo: String?,
    val shared: Boolean,
    val ratedAt: LocalDateTime?,

    // [Community/Social]
    val isScrapped: Boolean = false, // 라운지 조회 시 현재 유저의 스크랩 여부
    val ownerNickname: String? = null // 라운지에서 작성자 확인용
)
