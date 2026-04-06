package com.turt1e18.rwhiskey.rwhiskey.api.user.repository

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.UserToken
import org.springframework.data.jpa.repository.JpaRepository

interface UserTokenRepository : JpaRepository<UserToken, Int>