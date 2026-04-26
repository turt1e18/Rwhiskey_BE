package com.turt1e18.rwhiskey.rwhiskey.api.whiskey.repository

import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.entity.WhiskeyMaster
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WhiskeyMasterRepository : JpaRepository<WhiskeyMaster, Int> {
    fun findByWhiskeyName(whiskeyName: String): WhiskeyMaster?
}
