package com.turt1e18.rwhiskey.rwhiskey.api.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "user_token")
class UserToken(

    @Id
    @Column(name = "uid")
    var uid: Int,

    @Column(name="daily_limit")
    var dailyLimit: Int = 10,

    @Column(name="used_count")
    var usedCount: Int = 0,

    @Column(name="last_reset_time")
    var lastResetTime: LocalDate = LocalDate.now(),
) {
    /**
     * 날짜가 바뀌었는지 확인하고 사용 횟수를 초기화합니다.
     */
    fun checkAndReset() {
        val today = LocalDate.now()
        if (lastResetTime.isBefore(today)) {
            usedCount = 0
            lastResetTime = today
        }
    }

    /**
     * 토큰 사용 가능 여부를 확인합니다.
     */
    fun canUse(): Boolean {
        checkAndReset()
        return usedCount < dailyLimit
    }

    /**
     * 토큰을 1회 사용합니다.
     */
    fun use() {
        if (!canUse()) {
            throw IllegalStateException("Daily recommendation limit reached")
        }
        usedCount++
    }
}
