package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.service

import com.turt1e18.rwhiskey.rwhiskey.api.note.entity.TastingNote
import com.turt1e18.rwhiskey.rwhiskey.api.note.repository.TastingNoteRepository
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.request.RecommendationSaveRequest
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.dto.response.RecommendationDetailResponse
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.ResponseResult
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.UserRequest
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository.ResponseResultRepository
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository.UserRequestRepository
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
    private val tastingNoteRepository: TastingNoteRepository,
    private val userTokenRepository: UserTokenRepository,
    private val categoryTagRepository: CategoryTagRepository,
    private val flavorTagRepository: FlavorTagRepository,
    private val regionTagRepository: RegionTagRepository,
    private val styleTagRepository: StyleTagRepository
) {

    @Transactional
    fun saveRecommendation(uid: Int, requestDto: RecommendationSaveRequest): Int {
        val user = userRepository.findById(uid).orElseThrow { IllegalArgumentException("User not found") }

        // 1. 태그 자동 매핑 (이름으로 ID 조회, 없으면 새로 저장하여 Label 보존)
        val categoryId = requestDto.whiskeyCategory?.let { label ->
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
        }?.filterNotNull() // List<Int?>? -> List<Int>?

        // 2. 위스키 마스터 데이터 자동 업데이트 (위스키 이름 기준)
        val whiskeyName = requestDto.whiskeyName
        val existingWhiskey = whiskeyMasterRepository.findByWhiskeyName(whiskeyName)

        if (existingWhiskey == null) {
            val newWhiskey = WhiskeyMaster(
                wid = null,
                whiskeyName = whiskeyName,
                whiskeyNameEn = requestDto.whiskeyNameEn,
                mainTag = requestDto.mainTag,
                whiskeyCategory = categoryId,
                whiskeyFlavor = flavorIds, // 정규화된 ID 리스트 저장
                whiskeyRegion = regionId,
                whiskeyStyle = styleId
            )
            whiskeyMasterRepository.save(newWhiskey)
        } else {
            // 이미 존재한다면 누락된 정보만 업데이트
            var isUpdated = false
            if (existingWhiskey.whiskeyNameEn == null && requestDto.whiskeyNameEn != null) {
                existingWhiskey.whiskeyNameEn = requestDto.whiskeyNameEn
                isUpdated = true
            }
            if (existingWhiskey.mainTag == null && requestDto.mainTag != null) {
                existingWhiskey.mainTag = requestDto.mainTag
                isUpdated = true
            }
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
            if (existingWhiskey.whiskeyFlavor == null && flavorIds != null) {
                existingWhiskey.whiskeyFlavor = flavorIds
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
            flexFlag = requestDto.flexFlag,
            mainTag = requestDto.mainTag
        )
        val savedRequest = userRequestRepository.save(userRequest)

        // 4. 결과 정보 저장 (response_result)
        val responseResult = ResponseResult(
            rid = null,
            userRequest = savedRequest,
            user = user,
            whiskeyName = whiskeyName,
            whiskeyNameEn = requestDto.whiskeyNameEn,
            whiskeyCategory = requestDto.whiskeyCategory,
            featureTags = requestDto.featureTags,
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
    fun deleteRecommendation(uid: Int, oid: Int) {
        val userRequest = userRequestRepository.findById(oid)
            .orElseThrow { IllegalArgumentException("Order not found with ID: $oid") }

        if (userRequest.user.uid != uid) {
            throw IllegalArgumentException("해당 추천 결과에 대한 접근 권한이 없습니다.")
        }

        responseResultRepository.deleteByUserRequestOid(oid)
        userRequestRepository.delete(userRequest)
    }

    fun getNextOrderNumber(): Int {
        return userRequestRepository.findMaxOid() + 1
    }

    @Transactional(readOnly = true)
    fun getRecommendationDetail(uid: Int, oid: Int): RecommendationDetailResponse {
        val userRequest = userRequestRepository.findById(oid)
            .orElseThrow { IllegalArgumentException("Order not found with ID: $oid") }

        if (userRequest.user.uid != uid) {
            throw IllegalArgumentException("해당 추천 결과에 대한 접근 권한이 없습니다.")
        }

        val result = responseResultRepository.findByUserRequestOid(oid)
            ?: throw IllegalArgumentException("Result not found for Order ID: $oid")

        return RecommendationDetailResponse(
            oid = userRequest.oid!!,
            orderDate = userRequest.orderDate,
            weatherValue = userRequest.weatherValue,
            moodValue = userRequest.moodValue,
            abvValue = userRequest.abvValue,
            additionalValue = userRequest.additionalValue,
            flexFlag = userRequest.flexFlag,
            mainTag = userRequest.mainTag,
            whiskeyName = result.whiskeyName,
            whiskeyNameEn = result.whiskeyNameEn,
            whiskeyCategory = result.whiskeyCategory,
            featureTags = result.featureTags,
            foodName = result.foodName,
            pairingNote = result.pairingNote,
            bartenderWord = result.bartenderWord
        )
    }
}
