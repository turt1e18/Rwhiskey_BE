package com.turt1e18.rwhiskey.rwhiskey.api.recommendation.entity

import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_requests")
class UserRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid")
    var oid: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    var user: User,

    @Column(name = "weather_value")
    var weatherValue: String?,

    @Column(name = "mood_value")
    var moodValue: String?,

    @Column(name = "abv_value")
    var abvValue: String?,

    @Column(name = "additional_value", columnDefinition = "TEXT")
    var additionalValue: String?,

    @Column(name = "flex_flag")
    var flexFlag: Boolean = false,

    @Column(name = "main_tag")
    var mainTag: String? = null,

    @Column(name = "order_date")
    var orderDate: LocalDateTime = LocalDateTime.now()
)
