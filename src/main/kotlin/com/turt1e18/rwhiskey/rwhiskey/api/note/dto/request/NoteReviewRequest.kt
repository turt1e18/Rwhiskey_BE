package com.turt1e18.rwhiskey.rwhiskey.api.note.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

/**
 * 테이스팅 노트 리뷰 저장/수정 요청 DTO
 * 모든 필드에 @JsonProperty를 명시하여 프론트엔드와 백엔드 간의 필드 매핑 정합성을 보장합니다.
 */
data class NoteReviewRequest(
    @field:JsonProperty("reviewType")
    val reviewType: String? = null, // "BASIC" | "FREE"

    @field:JsonProperty("rating")
    @field:DecimalMin("0.0") @field:DecimalMax("5.0")
    val rating: Double? = null,

    @field:JsonProperty("nose")
    @field:Size(max = 500)
    val nose: String? = null,

    @field:JsonProperty("palate")
    @field:Size(max = 500)
    val palate: String? = null,

    @field:JsonProperty("finish")
    @field:Size(max = 500)
    val finish: String? = null,

    @field:JsonProperty("memo")
    @field:Size(max = 2000)
    val memo: String? = null,

    @field:JsonProperty("shared")
    val shared: Boolean? = null
)
