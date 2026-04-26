package com.turt1e18.rwhiskey.rwhiskey.api.whiskey.repository

import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface CategoryTagRepository : JpaRepository<CategoryTag, Int> {
    fun findByLabel(label: String): CategoryTag?
}

@Repository interface FlavorTagRepository : JpaRepository<FlavorTag, Int> {
    fun findByLabel(label: String): FlavorTag?
}

@Repository interface RegionTagRepository : JpaRepository<RegionTag, Int> {
    fun findByLabel(label: String): RegionTag?
}

@Repository interface StyleTagRepository : JpaRepository<StyleTag, Int> {
    fun findByLabel(label: String): StyleTag?
}
