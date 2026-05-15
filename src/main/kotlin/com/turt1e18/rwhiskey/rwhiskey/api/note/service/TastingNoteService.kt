package com.turt1e18.rwhiskey.rwhiskey.api.note.service

import com.turt1e18.rwhiskey.rwhiskey.api.note.dto.request.NoteReviewRequest
import com.turt1e18.rwhiskey.rwhiskey.api.note.dto.response.NoteResponse
import com.turt1e18.rwhiskey.rwhiskey.api.note.entity.NoteScrap
import com.turt1e18.rwhiskey.rwhiskey.api.note.entity.TastingNote
import com.turt1e18.rwhiskey.rwhiskey.api.note.repository.NoteScrapRepository
import com.turt1e18.rwhiskey.rwhiskey.api.note.repository.TastingNoteRepository
import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.repository.ResponseResultRepository
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TastingNoteService(
    private val tastingNoteRepository: TastingNoteRepository,
    private val noteScrapRepository: NoteScrapRepository,
    private val responseResultRepository: ResponseResultRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createNote(uid: Int, oid: Int): NoteResponse {
        // 이미 저장된 노트(북마크)가 있는지 확인
        if (tastingNoteRepository.existsByUserUidAndUserRequestOid(uid, oid)) {
            val existing = tastingNoteRepository.findByUserUidAndUserRequestOid(uid, oid)!!
            val isScrapped = noteScrapRepository.existsByUserAndTastingNote(
                userRepository.getReferenceById(uid), existing
            )
            return convertToResponse(existing, isScrapped)
        }

        // 추천 결과 조회
        val responseResult = responseResultRepository.findByUserRequestOid(oid)
            ?: throw IllegalArgumentException("해당 주문 번호(OID: $oid)에 대한 추천 결과를 찾을 수 없습니다.")

        // 권한 확인 (본인의 추천 결과인지)
        if (responseResult.user.uid != uid) {
            throw IllegalArgumentException("해당 추천 결과에 대한 접근 권한이 없습니다.")
        }

        val user = userRepository.findById(uid).orElseThrow { IllegalArgumentException("유저를 찾을 수 없습니다.") }

        // 테이스팅 노트로 저장 (스냅샷 생성)
        val note = TastingNote(
            user = user,
            userRequest = responseResult.userRequest,
            whiskeyName = responseResult.whiskeyName,
            whiskeyNameEn = responseResult.whiskeyNameEn,
            whiskeyCategory = responseResult.whiskeyCategory,
            regionId = responseResult.regionId,
            styleId = responseResult.styleId,
            featureTags = responseResult.featureTags,
            pairingNote = responseResult.pairingNote,
            bartenderWord = responseResult.bartenderWord,
            foodName = responseResult.foodName,
            additionalValue = responseResult.userRequest.additionalValue,
            recommendedAt = responseResult.orderDate
        )

        val saved = tastingNoteRepository.save(note)
        // 새 노트는 스크랩 상태가 false임
        return convertToResponse(saved, false)
    }

    @Transactional(readOnly = true)
    fun getMyNotes(uid: Int): List<NoteResponse> {
        val notes = tastingNoteRepository.findAllByUserUidOrderByRecommendedAtDesc(uid)
        val scrappedNoteIds = noteScrapRepository.findScrappedNoteIdsByUid(uid)
        
        return notes.map { convertToResponse(it, scrappedNoteIds) }
    }

    @Transactional(readOnly = true)
    fun getNoteDetail(uid: Int, noteId: Int): NoteResponse {
        val note = tastingNoteRepository.findById(noteId)
            .orElseThrow { IllegalArgumentException("노트를 찾을 수 없습니다.") }
        
        // 내 노트이거나, 공유된 상태여야 함
        if (note.user.uid != uid && !note.shared) {
            throw IllegalArgumentException("해당 노트에 접근할 권한이 없습니다.")
        }

        val isScrapped = noteScrapRepository.existsByUserAndTastingNote(
            userRepository.getReferenceById(uid), note
        )

        return convertToResponse(note, isScrapped)
    }

    @Transactional
    fun updateReview(uid: Int, noteId: Int, request: NoteReviewRequest): NoteResponse {
        val note = tastingNoteRepository.findById(noteId)
            .orElseThrow { IllegalArgumentException("노트를 찾을 수 없습니다.") }

        if (note.user.uid != uid) {
            throw IllegalArgumentException("본인의 노트만 수정할 수 있습니다.")
        }

        // 1. 후기 타입(BASIC/FREE) 전환 시 기존 데이터 선제적 정제
        // 타입이 변경되는 경우에만 상반되는 기존 데이터를 초기화합니다.
        request.reviewType?.let { newType ->
            if (note.reviewType != newType) {
                if (newType == "FREE") {
                    note.nose = null
                    note.palate = null
                    note.finish = null
                } else if (newType == "BASIC") {
                    note.memo = null
                }
                note.reviewType = newType
            }
        }

        // 2. 새 값 반영 (Null이 아닌 필드만 덮어쓰기)
        // 위에서 정제된 필드라도 요청에 새 값이 있다면 그 값이 최종 반영됩니다.
        request.rating?.let { note.rating = it }
        request.nose?.let { note.nose = it }
        request.palate?.let { note.palate = it }
        request.finish?.let { note.finish = it }
        request.memo?.let { note.memo = it }
        request.shared?.let { note.shared = it }

        note.ratedAt = LocalDateTime.now()

        val saved = tastingNoteRepository.save(note)
        val isScrapped = noteScrapRepository.existsByUserAndTastingNote(
            userRepository.getReferenceById(uid), saved
        )
        return convertToResponse(saved, isScrapped)
    }

    @Transactional
    fun deleteNote(uid: Int, noteId: Int) {
        val note = tastingNoteRepository.findById(noteId)
            .orElseThrow { IllegalArgumentException("노트를 찾을 수 없습니다.") }

        if (note.user.uid != uid) {
            throw IllegalArgumentException("본인의 노트만 삭제할 수 있습니다.")
        }

        tastingNoteRepository.delete(note)
    }

    @Transactional(readOnly = true)
    fun getLoungeFeed(uid: Int): List<NoteResponse> {
        val notes = tastingNoteRepository.findBySharedTrueOrderByRatedAtDesc()
        val scrappedNoteIds = noteScrapRepository.findScrappedNoteIdsByUid(uid)
        
        return notes.map { convertToResponse(it, scrappedNoteIds) }
    }

    @Transactional
    fun toggleScrap(uid: Int, noteId: Int): Boolean {
        val user = userRepository.findById(uid)
            .orElseThrow { IllegalArgumentException("유저를 찾을 수 없습니다.") }
        val note = tastingNoteRepository.findById(noteId)
            .orElseThrow { IllegalArgumentException("노트를 찾을 수 없습니다.") }

        if (!note.shared && note.user.uid != uid) {
            throw IllegalArgumentException("공유되지 않은 노트는 스크랩할 수 없습니다.")
        }

        return if (noteScrapRepository.existsByUserAndTastingNote(user, note)) {
            noteScrapRepository.deleteByUserAndTastingNote(user, note)
            false
        } else {
            noteScrapRepository.save(NoteScrap(user = user, tastingNote = note))
            true
        }
    }

    @Transactional(readOnly = true)
    fun getScrappedNotes(uid: Int): List<NoteResponse> {
        val scraps = noteScrapRepository.findAllByUserUidOrderByScrappedAtDesc(uid)
        val scrappedNoteIds = scraps.map { it.tastingNote.id!! }.toSet()
        
        return scraps.map { convertToResponse(it.tastingNote, scrappedNoteIds) }
    }

    /**
     * 목록 변환용 (N+1 방지)
     */
    private fun convertToResponse(note: TastingNote, scrappedNoteIds: Set<Int>): NoteResponse {
        return convertToResponse(note, scrappedNoteIds.contains(note.id))
    }

    /**
     * 단일 변환용
     */
    private fun convertToResponse(note: TastingNote, isScrapped: Boolean): NoteResponse {
        return NoteResponse(
            id = note.id!!,
            oid = note.userRequest.oid!!,
            status = if (note.ratedAt != null) "평가완료" else "미평가",
            whiskeyName = note.whiskeyName,
            whiskeyNameEn = note.whiskeyNameEn,
            whiskeyCategory = note.whiskeyCategory,
            regionId = note.regionId,
            styleId = note.styleId,
            featureTags = note.featureTags,
            foodName = note.foodName,
            additionalValue = note.additionalValue,
            pairingNote = note.pairingNote,
            bartenderWord = note.bartenderWord,
            weatherValue = note.userRequest.weatherValue,
            moodValue = note.userRequest.moodValue,
            abvValue = note.userRequest.abvValue,
            recommendedAt = note.recommendedAt,
            reviewType = note.reviewType,
            rating = note.rating,
            nose = note.nose,
            palate = note.palate,
            finish = note.finish,
            memo = note.memo,
            shared = note.shared,
            ratedAt = note.ratedAt,
            isScrapped = isScrapped,
            ownerNickname = note.user.name
        )
    }
}
