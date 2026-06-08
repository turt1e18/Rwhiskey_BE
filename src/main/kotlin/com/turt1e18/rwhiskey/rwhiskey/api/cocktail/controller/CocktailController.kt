package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserPrincipal
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.dto.request.CocktailSaveRequest
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.dto.response.CocktailDetailResponse
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.service.CocktailService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cocktails")
class CocktailController(
    private val cocktailService: CocktailService
) {

    @PostMapping
    fun saveCocktailResult(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @RequestBody request: CocktailSaveRequest
    ): ResponseEntity<Int> {
        val oid = cocktailService.saveCocktailResult(principal.uid, request)
        return ResponseEntity.ok(oid)
    }

    @GetMapping("/{oid}")
    fun getCocktailDetail(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @PathVariable oid: Int
    ): ResponseEntity<CocktailDetailResponse> {
        return ResponseEntity.ok(cocktailService.getCocktailDetail(principal.uid, oid))
    }

    @DeleteMapping("/{oid}")
    fun deleteCocktailResult(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @PathVariable oid: Int
    ): ResponseEntity<Void> {
        cocktailService.deleteCocktailResult(principal.uid, oid)
        return ResponseEntity.noContent().build()
    }
}
