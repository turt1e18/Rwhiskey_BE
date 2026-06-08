package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.repository

import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.entity.CocktailRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CocktailRequestRepository : JpaRepository<CocktailRequest, Int>
