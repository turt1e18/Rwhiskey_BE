package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository

import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.ResponseResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResponseResultRepository : JpaRepository<ResponseResult, Int> {
    fun findByUserRequestOid(oid: Int): ResponseResult?
    fun deleteByUserRequestOid(oid: Int)
}
