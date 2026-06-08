package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.dto.response

import java.time.LocalDateTime

data class CocktailDetailResponse(
    val oid: Int,
    val orderDate: LocalDateTime,
    
    // Request info
    val experienceLevel: String?,
    val isNonAlcoholic: Boolean,
    val preferredTaste: List<String>?,
    val carbonation: Boolean?,
    val dislikes: List<String>?,
    val currentMood: String?,
    val requestBaseSpirit: String?,

    // Response info
    val cocktailName: String,
    val responseBaseSpirit: String?,
    val abv: String?,
    val foodName: String?,
    val pairingNote: String?,
    val bartenderWord: String?,
    
    // Optional enriched fields
    val images: List<String>?,
    val checkList: List<String>?,
    val method: List<String>?
)
