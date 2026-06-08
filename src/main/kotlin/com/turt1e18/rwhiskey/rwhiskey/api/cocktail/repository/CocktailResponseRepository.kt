package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.repository

import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.entity.CocktailResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CocktailResponseRepository : JpaRepository<CocktailResponse, Int> {
    fun findByCocktailRequestOid(oid: Int): CocktailResponse?
    fun deleteByCocktailRequestOid(oid: Int)
}
