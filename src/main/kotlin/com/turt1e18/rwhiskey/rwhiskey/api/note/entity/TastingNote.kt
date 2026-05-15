package com.turt1e18.rwhiskey.rwhiskey.api.note.entity

import com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity.UserRequest
import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "tasting_notes")
class TastingNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    var user: User,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid", nullable = false)
    var userRequest: UserRequest,

    // [Part A: Recommendation Context - Captured at save time]
    @Column(name = "w_name")
    var whiskeyName: String,

    @Column(name = "w_name_en")
    var whiskeyNameEn: String?,

    @Column(name = "w_category", length = 100)
    var whiskeyCategory: String?,

    @Column(name = "w_region")
    var regionId: Int?,

    @Column(name = "w_style")
    var styleId: Int?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "w_flavor", columnDefinition = "JSONB")
    var featureTags: List<String>?,

    @Column(name = "reason", columnDefinition = "TEXT")
    var pairingNote: String?,

    @Column(name = "bartender", columnDefinition = "TEXT")
    var bartenderWord: String?,

    @Column(name = "snack")
    var foodName: String?,

    @Column(name = "additional_value", columnDefinition = "TEXT")
    var additionalValue: String?,

    @Column(name = "recommended_at")
    var recommendedAt: LocalDateTime = LocalDateTime.now(),

    // [Part B: User Review - Mutable]
    @Column(name = "review_type", length = 20)
    var reviewType: String? = "BASIC", // "BASIC" | "FREE"

    @Column(name = "rating")
    var rating: Double? = 0.0,

    @Column(name = "nose", length = 500)
    var nose: String? = null,

    @Column(name = "palate", length = 500)
    var palate: String? = null,

    @Column(name = "finish", length = 500)
    var finish: String? = null,

    @Column(name = "memo", columnDefinition = "TEXT")
    var memo: String? = null,

    @Column(name = "shared")
    var shared: Boolean = false,

    @Column(name = "rated_at")
    var ratedAt: LocalDateTime? = null
)
