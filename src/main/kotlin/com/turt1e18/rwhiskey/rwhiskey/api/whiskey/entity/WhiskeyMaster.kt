package com.turt1e18.rwhiskey.rwhiskey.api.whiskey.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "whiskey_master")
class WhiskeyMaster(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wid")
    var wid: Int? = null,

    @Column(name = "w_name", nullable = false, unique = true)
    var whiskeyName: String,

    @Column(name = "w_name_en")
    var whiskeyNameEn: String? = null,

    @Column(name = "w_main_tag")
    var mainTag: String? = null,

    @Column(name = "w_category")
    var whiskeyCategory: Int?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "w_flavor", columnDefinition = "JSONB")
    var whiskeyFlavor: List<Int>?,

    @Column(name = "w_region")
    var whiskeyRegion: Int?,

    @Column(name = "w_style")
    var whiskeyStyle: Int?
)
