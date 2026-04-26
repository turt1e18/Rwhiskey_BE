package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.controller

import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request.RecommendationSaveRequest
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.service.RecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recommendations")
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @PostMapping("/{uid}")
    fun createRecommendation(
        @PathVariable uid: Int,
        @RequestBody request: RecommendationSaveRequest
    ): ResponseEntity<Int> {
        val oid = recommendationService.saveRecommendation(uid, request)
        return ResponseEntity.ok(oid)
    }

    @DeleteMapping("/{oid}")
    fun deleteRecommendation(
        @PathVariable oid: Int
    ): ResponseEntity<Void> {
        recommendationService.deleteRecommendation(oid)
        return ResponseEntity.noContent().build()
    }
}
