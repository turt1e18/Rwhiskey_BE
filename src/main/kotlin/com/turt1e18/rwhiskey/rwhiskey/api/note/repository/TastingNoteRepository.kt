package com.turt1e18.rwhiskey.rwhiskey.api.note.repository

import com.turt1e18.rwhiskey.rwhiskey.api.note.entity.TastingNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TastingNoteRepository : JpaRepository<TastingNote, Int> {
    fun findAllByUserUidOrderByRecommendedAtDesc(uid: Int): List<TastingNote>
    fun findBySharedTrueOrderByRatedAtDesc(): List<TastingNote>
    fun existsByUserUidAndUserRequestOid(uid: Int, oid: Int): Boolean
    fun findByUserUidAndUserRequestOid(uid: Int, oid: Int): TastingNote?
}
