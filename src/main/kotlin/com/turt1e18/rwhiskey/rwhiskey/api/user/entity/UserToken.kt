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
    var dailyLimit: Int = 3,

    @Column(name="used_count")
    var usedCount: Int = 0,

    @Column(name="last_reset_time")
    var lastResetTime: LocalDate = LocalDate.now(),
)
