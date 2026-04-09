package com.turt1e18.rwhiskey.rwhiskey.api.user.repository

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun existsByEmail(email: String): Boolean
    fun findByEmailAndDeleteDateIsNull(email: String): User?
}
