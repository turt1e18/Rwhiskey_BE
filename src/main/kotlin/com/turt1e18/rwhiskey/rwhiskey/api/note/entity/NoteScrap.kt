package com.turt1e18.rwhiskey.rwhiskey.api.note.entity

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "note_scraps")
class NoteScrap(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    var tastingNote: TastingNote,

    @Column(name = "scrapped_at")
    var scrappedAt: LocalDateTime = LocalDateTime.now()
)
