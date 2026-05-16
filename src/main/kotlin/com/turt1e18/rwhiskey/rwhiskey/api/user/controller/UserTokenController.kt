package com.turt1e18.rwhiskey.rwhiskey.api.user.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserPrincipal
import com.turt1e18.rwhiskey.rwhiskey.api.user.service.UserTokenService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/token")
class UserTokenController(
    private val userTokenService: UserTokenService
) {

    /**
     * 현재 사용자의 토큰 상태(남은 횟수 등) 조회
     */
    @GetMapping
    fun getTokenStatus(
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<Map<String, Any>> {
        val token = userTokenService.getTokenStatus(principal.uid)
        return ResponseEntity.ok(mapOf(
            "daily_limit" to token.dailyLimit,
            "used_count" to token.usedCount,
            "remaining" to (token.dailyLimit - token.usedCount)
        ))
    }

    /**
     * 토큰 차감 API
     * 추천 프로세스 시작 전 호출하여 권한 확인 및 차감 수행
     */
    @PostMapping("/decrement")
    fun decrementToken(
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<Map<String, Any>> {
        val success = userTokenService.decrementToken(principal.uid)
        return if (success) {
            ResponseEntity.ok(mapOf("success" to true, "message" to "Token decremented"))
        } else {
            // 403 대신 429(Too Many Requests)를 반환하여 보안 에러와 구분
            ResponseEntity.status(429).body(mapOf("success" to false, "message" to "Daily limit reached"))
        }
    }

    /**
     * 토큰 복구 API (임시 조치용)
     * AI 호출 실패 등 예외 상황에서 프론트엔드가 호출
     */
    @PostMapping("/increment")
    fun incrementToken(
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<Map<String, Any>> {
        val (success, message) = userTokenService.incrementToken(principal.uid)
        return if (success) {
            ResponseEntity.ok(mapOf("success" to true, "message" to message))
        } else {
            ResponseEntity.status(400).body(mapOf("success" to false, "message" to message))
        }
    }
}
