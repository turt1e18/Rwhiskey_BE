package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.service

import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.dto.request.CocktailSaveRequest
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.dto.response.CocktailDetailResponse
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.entity.CocktailRequest
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.entity.CocktailResponse
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.repository.CocktailRequestRepository
import com.turt1e18.rwhiskey.rwhiskey.api.cocktail.repository.CocktailResponseRepository
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CocktailService(
    private val userRepository: UserRepository,
    private val cocktailRequestRepository: CocktailRequestRepository,
    private val cocktailResponseRepository: CocktailResponseRepository
) {

    @Transactional
    fun saveCocktailResult(uid: Int, requestDto: CocktailSaveRequest): Int {
        val user = userRepository.findById(uid).orElseThrow { IllegalArgumentException("User not found") }

        // 1. Save Cocktail Request
        val cocktailRequest = CocktailRequest(
            user = user,
            experienceLevel = requestDto.experienceLevel,
            isNonAlcoholic = requestDto.isNonAlcoholic,
            preferredTaste = requestDto.preferredTaste,
            carbonation = requestDto.carbonation,
            dislikes = requestDto.dislikes,
            currentMood = requestDto.currentMood,
            baseSpirit = requestDto.requestBaseSpirit
        )
        val savedRequest = cocktailRequestRepository.save(cocktailRequest)

        // 2. Save Cocktail Response
        val cocktailResponse = CocktailResponse(
            cocktailRequest = savedRequest,
            user = user,
            cocktailName = requestDto.cocktailName,
            baseSpirit = requestDto.responseBaseSpirit,
            abv = requestDto.abv,
            foodName = requestDto.foodName,
            bartenderWord = requestDto.bartenderWord,
            pairingNote = requestDto.pairingNote,
            images = requestDto.images,
            checkList = requestDto.checkList,
            method = requestDto.method
        )
        cocktailResponseRepository.save(cocktailResponse)

        return savedRequest.oid!!
    }

    @Transactional(readOnly = true)
    fun getCocktailDetail(uid: Int, oid: Int): CocktailDetailResponse {
        val request = cocktailRequestRepository.findById(oid)
            .orElseThrow { IllegalArgumentException("Cocktail request not found with ID: $oid") }

        if (request.user.uid != uid) {
            throw IllegalArgumentException("해당 결과에 대한 접근 권한이 없습니다.")
        }

        val response = cocktailResponseRepository.findByCocktailRequestOid(oid)
            ?: throw IllegalArgumentException("Cocktail response not found for request ID: $oid")

        return CocktailDetailResponse(
            oid = request.oid!!,
            orderDate = request.orderDate,
            experienceLevel = request.experienceLevel,
            isNonAlcoholic = request.isNonAlcoholic,
            preferredTaste = request.preferredTaste,
            carbonation = request.carbonation,
            dislikes = request.dislikes,
            currentMood = request.currentMood,
            requestBaseSpirit = request.baseSpirit,
            cocktailName = response.cocktailName,
            responseBaseSpirit = response.baseSpirit,
            abv = response.abv,
            foodName = response.foodName,
            bartenderWord = response.bartenderWord,
            pairingNote = response.pairingNote,
            images = response.images,
            checkList = response.checkList,
            method = response.method
        )
    }

    @Transactional
    fun deleteCocktailResult(uid: Int, oid: Int) {
        val request = cocktailRequestRepository.findById(oid)
            .orElseThrow { IllegalArgumentException("Cocktail request not found with ID: $oid") }

        if (request.user.uid != uid) {
            throw IllegalArgumentException("해당 결과에 대한 접근 권한이 없습니다.")
        }

        cocktailResponseRepository.deleteByCocktailRequestOid(oid)
        cocktailRequestRepository.delete(request)
    }
}
