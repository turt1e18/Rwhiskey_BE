package com.turt1e18.rwhiskey.rwhiskey.api.note.controller

import com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserPrincipal
import com.turt1e18.rwhiskey.rwhiskey.api.note.dto.request.NoteReviewRequest
import com.turt1e18.rwhiskey.rwhiskey.api.note.dto.response.NoteResponse
import com.turt1e18.rwhiskey.rwhiskey.api.note.service.TastingNoteService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TastingNoteController(
    private val tastingNoteService: TastingNoteService
) {

    /**
     * [내 기록] 추천 결과 저장 (북마크/Keep)
     * 추천 결과 화면에서 '저장' 버튼 클릭 시 호출
     */
    @PostMapping("/notes")
    fun saveRecommendation(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @RequestBody request: Map<String, Int>
    ): ResponseEntity<NoteResponse> {
        val oid = request["oid"] ?: throw IllegalArgumentException("oid는 필수입니다.")
        val response = tastingNoteService.createNote(principal.uid, oid)
        return ResponseEntity.ok(response)
    }

    /**
     * [내 기록] 테이스팅 노트 목록 조회
     */
    @GetMapping("/notes")
    fun getMyNotes(
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<List<NoteResponse>> {
        val response = tastingNoteService.getMyNotes(principal.uid)
        return ResponseEntity.ok(response)
    }

    /**
     * [내 기록] 특정 노트 상세 조회
     */
    @GetMapping("/notes/{id}")
    fun getNoteDetail(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @PathVariable id: Int
    ): ResponseEntity<NoteResponse> {
        val response = tastingNoteService.getNoteDetail(principal.uid, id)
        return ResponseEntity.ok(response)
    }

    /**
     * [내 기록] 후기(Part B) 저장 및 수정
     */
    @PatchMapping("/notes/{id}/review")
    fun updateReview(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @PathVariable id: Int,
        @Valid @RequestBody request: NoteReviewRequest
    ): ResponseEntity<NoteResponse> {
        val response = tastingNoteService.updateReview(principal.uid, id, request)
        return ResponseEntity.ok(response)
    }

    /**
     * [내 기록] 노트 삭제
     */
    @DeleteMapping("/notes/{id}")
    fun deleteNote(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @PathVariable id: Int
    ): ResponseEntity<Void> {
        tastingNoteService.deleteNote(principal.uid, id)
        return ResponseEntity.noContent().build()
    }

    /**
     * [라운지] 공유된 후기 목록 조회
     */
    @GetMapping("/lounge")
    fun getLoungeFeed(
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<List<NoteResponse>> {
        val response = tastingNoteService.getLoungeFeed(principal.uid)
        return ResponseEntity.ok(response)
    }

    /**
     * [라운지] 스크랩 토글 (좋아요/건배)
     */
    @PostMapping("/lounge/{id}/scrap")
    fun toggleScrap(
        @AuthenticationPrincipal principal: CustomUserPrincipal,
        @PathVariable id: Int
    ): ResponseEntity<Map<String, Boolean>> {
        val isScrapped = tastingNoteService.toggleScrap(principal.uid, id)
        return ResponseEntity.ok(mapOf("isScrapped" to isScrapped))
    }

    /**
     * [스크랩북] 내가 스크랩한 타인의 후기 목록
     */
    @GetMapping("/notes/scraps")
    fun getScrappedNotes(
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<List<NoteResponse>> {
        val response = tastingNoteService.getScrappedNotes(principal.uid)
        return ResponseEntity.ok(response)
    }
}
