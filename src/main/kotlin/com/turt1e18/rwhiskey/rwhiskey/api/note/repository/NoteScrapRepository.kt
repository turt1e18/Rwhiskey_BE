package com.turt1e18.rwhiskey.rwhiskey.api.note.repository

import com.turt1e18.rwhiskey.rwhiskey.api.note.entity.NoteScrap
import com.turt1e18.rwhiskey.rwhiskey.api.note.entity.TastingNote
import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteScrapRepository : JpaRepository<NoteScrap, Int> {
    fun existsByUserAndTastingNote(user: User, tastingNote: TastingNote): Boolean
    fun deleteByUserAndTastingNote(user: User, tastingNote: TastingNote)
    fun findAllByUserUidOrderByScrappedAtDesc(uid: Int): List<NoteScrap>

    /**
     * 특정 유저가 스크랩한 모든 노트 ID를 한 번에 조회합니다. (N+1 방지용)
     */
    @org.springframework.data.jpa.repository.Query("SELECT ns.tastingNote.id FROM NoteScrap ns WHERE ns.user.uid = :uid")
    fun findScrappedNoteIdsByUid(uid: Int): Set<Int>
}
