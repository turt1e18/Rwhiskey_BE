package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserPrincipal
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request.RecommendationSaveRequest
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.response.RecommendationDetailResponse
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.service.RecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recommendations")
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @PostMapping
    fun createRecommendation(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @RequestBody request: RecommendationSaveRequest
    ): ResponseEntity<Int> {
        val oid = recommendationService.saveRecommendation(principal.uid, request)
        return ResponseEntity.ok(oid)
    }

    @GetMapping("/next-no")
    fun getNextNo(): ResponseEntity<Int> {
        return ResponseEntity.ok(recommendationService.getNextOrderNumber())
    }

    @GetMapping("/{oid}")
    fun getRecommendation(
        @PathVariable oid: Int
    ): ResponseEntity<RecommendationDetailResponse> {
        return ResponseEntity.ok(recommendationService.getRecommendationDetail(oid))
    }

    @DeleteMapping("/{oid}")
    fun deleteRecommendation(
        @PathVariable oid: Int
    ): ResponseEntity<Void> {
        recommendationService.deleteRecommendation(oid)
        return ResponseEntity.noContent().build()
    }
}
