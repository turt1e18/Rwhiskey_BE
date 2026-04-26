package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request.RecommendationSaveRequest
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.ResponseResult
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.UserRequest
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository.ResponseResultRepository
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository.UserRequestRepository
import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.UserToken
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserRepository
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserTokenRepository
import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.entity.*
import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecommendationService(
    private val userRepository: UserRepository,
    private val userRequestRepository: UserRequestRepository,
    private val responseResultRepository: ResponseResultRepository,
    private val whiskeyMasterRepository: WhiskeyMasterRepository,
    private val userTokenRepository: UserTokenRepository,
    private val categoryTagRepository: CategoryTagRepository,
    private val flavorTagRepository: FlavorTagRepository,
    private val regionTagRepository: RegionTagRepository,
    private val styleTagRepository: StyleTagRepository,
    private val objectMapper: ObjectMapper // JSON 변환용
) {

    @Transactional
    fun saveRecommendation(uid: Int, requestDto: RecommendationSaveRequest): Int {
        val user = userRepository.findById(uid).orElseThrow { IllegalArgumentException("User not found") }

        // 0. 토큰 차감 및 제한 확인
        val userToken = userTokenRepository.findById(uid).orElseGet {
            userTokenRepository.save(UserToken(uid = uid))
        }
        
        userToken.use()
        userTokenRepository.save(userToken)

        // 1. 태그 자동 매핑 (이름으로 ID 조회, 없으면 새로 저장하여 Label 보존)
        val categoryId = requestDto.classification?.let { label ->
            categoryTagRepository.findByLabel(label)?.id 
                ?: categoryTagRepository.save(CategoryTag(label = label)).id
        }

        val regionId = requestDto.regionName?.let { label ->
            regionTagRepository.findByLabel(label)?.id 
                ?: regionTagRepository.save(RegionTag(label = label)).id
        }

        val styleId = requestDto.styleName?.let { label ->
            styleTagRepository.findByLabel(label)?.id 
                ?: styleTagRepository.save(StyleTag(label = label)).id
        }

        // 1-1. 맛 태그(Flavor) 자동 매핑 (텍스트 리스트 -> ID 리스트)
        val flavorIds = requestDto.featureTags?.map { label ->
            flavorTagRepository.findByLabel(label)?.id
                ?: flavorTagRepository.save(FlavorTag(label = label)).id
        }
        val flavorIdsJson = flavorIds?.let { objectMapper.writeValueAsString(it) }

        // 2. 위스키 마스터 데이터 자동 업데이트 (위스키 이름 기준)
        val whiskeyName = requestDto.whiskyName
        val existingWhiskey = whiskeyMasterRepository.findByWhiskeyName(whiskeyName)

        if (existingWhiskey == null) {
            val newWhiskey = WhiskeyMaster(
                wid = null,
                whiskeyName = whiskeyName,
                whiskeyCategory = categoryId,
                whiskeyFlavor = flavorIdsJson, // 정규화된 ID 리스트 저장
                whiskeyRegion = regionId,
                whiskeyStyle = styleId
            )
            whiskeyMasterRepository.save(newWhiskey)
        } else {
            // 이미 존재한다면 누락된 정보만 업데이트
            var isUpdated = false
            if (existingWhiskey.whiskeyCategory == null && categoryId != null) {
                existingWhiskey.whiskeyCategory = categoryId
                isUpdated = true
            }
            if (existingWhiskey.whiskeyRegion == null && regionId != null) {
                existingWhiskey.whiskeyRegion = regionId
                isUpdated = true
            }
            if (existingWhiskey.whiskeyStyle == null && styleId != null) {
                existingWhiskey.whiskeyStyle = styleId
                isUpdated = true
            }
            if (existingWhiskey.whiskeyFlavor == null && flavorIdsJson != null) {
                existingWhiskey.whiskeyFlavor = flavorIdsJson
                isUpdated = true
            }
            if (isUpdated) {
                whiskeyMasterRepository.save(existingWhiskey)
            }
        }

        // 3. 요청 정보 저장 (user_requests)
        val userRequest = UserRequest(
            user = user,
            weatherValue = requestDto.weatherValue,
            moodValue = requestDto.moodValue,
            abvValue = requestDto.abvValue,
            additionalValue = requestDto.additionalValue,
            flexFlag = requestDto.flexFlag
        )
        val savedRequest = userRequestRepository.save(userRequest)

        // 4. 결과 정보 저장 (response_result)
        // featureTags는 AI가 준 원본 텍스트 리스트를 JSON으로 저장 (히스토리 보존용)
        val originalTagsJson = requestDto.featureTags?.let { objectMapper.writeValueAsString(it) }
        
        val responseResult = ResponseResult(
            rid = null,
            userRequest = savedRequest,
            user = user,
            whiskyName = whiskeyName,
            whiskyNameEn = requestDto.whiskyNameEn,
            classification = requestDto.classification,
            featureTags = originalTagsJson,
            regionId = regionId, // 지역 ID 저장
            styleId = styleId,   // 스타일 ID 저장
            foodName = requestDto.foodName,
            pairingNote = requestDto.pairingNote,
            bartenderWord = requestDto.bartenderWord
        )
        responseResultRepository.save(responseResult)

        return savedRequest.oid!!
    }

    @Transactional
    fun deleteRecommendation(oid: Int) {
        responseResultRepository.deleteByUserRequestOid(oid)
        userRequestRepository.deleteById(oid)
    }
}
