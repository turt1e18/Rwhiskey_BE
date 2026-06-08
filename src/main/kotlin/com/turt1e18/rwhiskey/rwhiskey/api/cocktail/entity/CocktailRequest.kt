package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.entity

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "cocktail_requests")
class CocktailRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid")
    var oid: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    var user: User,

    @Column(name = "experience_level", length = 50)
    var experienceLevel: String?,

    @Column(name = "is_non_alcoholic")
    var isNonAlcoholic: Boolean = false,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_taste", columnDefinition = "JSONB")
    var preferredTaste: List<String>?,

    @Column(name = "carbonation")
    var carbonation: Boolean?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dislikes", columnDefinition = "JSONB")
    var dislikes: List<String>?,

    @Column(name = "current_mood", columnDefinition = "TEXT")
    var currentMood: String?,

    @Column(name = "base_spirit", length = 255)
    var baseSpirit: String?,

    @Column(name = "order_date")
    var orderDate: LocalDateTime = LocalDateTime.now()
)
