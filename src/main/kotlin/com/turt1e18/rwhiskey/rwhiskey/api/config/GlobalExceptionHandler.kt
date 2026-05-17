package com.turt1e18.rwhiskey.rwhiskey.api.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val slackService: SlackService
) {

    /**
     * 비즈니스 로직 오류 (예: 유저 없음, 권한 없음 등)
     */
    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleBadRequest(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                success = false,
                message = e.message ?: "잘못된 요청입니다."
            ))
    }

    /**
     * 예상치 못한 서버 내부 오류 (보안상 상세 정보 노출 차단 + Slack 알림)
     */
    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(e: Exception, request: jakarta.servlet.http.HttpServletRequest): ResponseEntity<ErrorResponse> {
        // 유저 정보 추출 (로그인 상태일 경우)
        val authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().authentication
        val userIdentifier = if (authentication != null && authentication.isAuthenticated && authentication.principal is com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserPrincipal) {
            val principal = authentication.principal as com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserPrincipal
            "UID: ${principal.uid} / Email: ${principal.username}"
        } else {
            "Guest"
        }

        // Slack 알림 전송 (API 정보 및 유저 정보 포함)
        slackService.sendErrorNotification(request.method, request.requestURI, e, userIdentifier)

        // 실제 운영 시에는 로깅 라이브러리를 통해 서버 로그에만 스택트레이스를 남겨야 함
        e.printStackTrace()

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                success = false,
                message = "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."
            ))
    }
    data class ErrorResponse(
        val success: Boolean,
        val message: String
    )
}
