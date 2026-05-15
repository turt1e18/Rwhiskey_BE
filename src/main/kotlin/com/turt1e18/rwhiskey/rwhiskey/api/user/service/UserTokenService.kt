package com.turt1e18.rwhiskey.rwhiskey.api.user.service

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.UserToken
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserTokenService(
    private val userTokenRepository: UserTokenRepository
) {

    @Transactional(readOnly = true)
    fun getTokenStatus(uid: Int): UserToken {
        val userToken = userTokenRepository.findById(uid).orElseGet {
            userTokenRepository.save(UserToken(uid = uid))
        }
        userToken.checkAndReset()
        return userToken
    }

    @Transactional
    fun decrementToken(uid: Int): Boolean {
        val userToken = getTokenStatus(uid)
        return try {
            userToken.use()
            userToken.lastActionTime = java.time.LocalDateTime.now() // 차감 시점 기록
            userTokenRepository.save(userToken)
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    /**
     * [임시/복구용] 토큰 복구 로직
     * 남용 방지: 최근 5분 이내에 차감 이력이 있는 경우에만 복구 허용
     */
    @Transactional
    fun incrementToken(uid: Int): Pair<Boolean, String> {
        val userToken = getTokenStatus(uid)
        
        if (userToken.usedCount <= 0) {
            return false to "복구할 토큰이 없습니다."
        }

        val lastAction = userToken.lastActionTime
        val now = java.time.LocalDateTime.now()

        // 보안 장치: 차감 후 5분이 지났거나 차감 이력이 없으면 복구 불가
        if (lastAction == null || lastAction.isBefore(now.minusMinutes(5))) {
            return false to "복구 가능 시간이 초과되었거나 이력이 없습니다. (최대 5분)"
        }

        userToken.usedCount--
        userToken.lastActionTime = null // 복구 완료 후 이력 초기화 (중복 복구 방지)
        userTokenRepository.save(userToken)
        
        return true to "토큰이 성공적으로 복구되었습니다."
    }
}
