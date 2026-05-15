package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "response_result")
class ResponseResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rid")
    var rid: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid", nullable = false)
    var userRequest: UserRequest,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    var user: User,

    @Column(name = "w_name")
    var whiskeyName: String,

    @Column(name = "w_name_en")
    var whiskeyNameEn: String?,

    @Column(name = "w_category")
    var whiskeyCategory: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "w_flavor", columnDefinition = "JSONB")
    var featureTags: List<String>?,

    @Column(name = "w_region")
    var regionId: Int?,

    @Column(name = "w_style")
    var styleId: Int?,

    @Column(name = "snack")
    var foodName: String?,

    @Column(name = "reason", columnDefinition = "TEXT")
    var pairingNote: String?,

    @Column(name = "bartender", columnDefinition = "TEXT")
    var bartenderWord: String?,

    @Column(name = "order_date")
    var orderDate: LocalDateTime = LocalDateTime.now()
)
