package com.turt1e18.rwhiskey.rwhiskey.api.cocktail.entity

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "cocktail_responses")
class CocktailResponse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rid")
    var rid: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid", nullable = false, unique = true)
    var cocktailRequest: CocktailRequest,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    var user: User,

    @Column(name = "cocktail_name", nullable = false, length = 255)
    var cocktailName: String,

    @Column(name = "base_spirit", length = 255)
    var baseSpirit: String?,

    @Column(name = "abv", length = 255)
    var abv: String?,

    @Column(name = "food_pairing", length = 255)
    var foodName: String?,

    @Column(name = "bartender_message", columnDefinition = "TEXT")
    var bartenderWord: String?,

    @Column(name = "tasting_note", columnDefinition = "TEXT")
    var pairingNote: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "JSONB")
    var images: List<String>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "check_list", columnDefinition = "JSONB")
    var checkList: List<String>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "method", columnDefinition = "JSONB")
    var method: List<String>? = null,

    @Column(name = "order_date")
    var orderDate: LocalDateTime = LocalDateTime.now()
)
