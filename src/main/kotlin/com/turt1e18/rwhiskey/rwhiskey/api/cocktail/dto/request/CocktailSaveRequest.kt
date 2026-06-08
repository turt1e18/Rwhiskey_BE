package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CocktailSaveRequest(
    // Request Fields
    val experienceLevel: String?,
    @JsonProperty("isNonAlcoholic")
    val isNonAlcoholic: Boolean = false,
    val preferredTaste: List<String>?,
    val carbonation: Boolean?,
    val dislikes: List<String>?,
    val currentMood: String?,
    @JsonProperty("requestBaseSpirit")
    val requestBaseSpirit: String?,

    // Response Fields (AI Result & Enrichment)
    val cocktailName: String,
    @JsonProperty("responseBaseSpirit")
    val responseBaseSpirit: String?,
    val abv: String?,
    
    // Semantically aligned with Whiskey domain and COCKTAIL_API_SPEC.md
    val foodName: String?,
    val pairingNote: String?,
    val bartenderWord: String?,
    
    // Optional enriched fields
    val images: List<String>? = null,
    val checkList: List<String>? = null,
    val method: List<String>? = null
)
