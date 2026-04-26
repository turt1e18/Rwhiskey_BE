package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository

import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.UserRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRequestRepository : JpaRepository<UserRequest, Int> {
    fun findAllByUserUid(uid: Int): List<UserRequest>
}
