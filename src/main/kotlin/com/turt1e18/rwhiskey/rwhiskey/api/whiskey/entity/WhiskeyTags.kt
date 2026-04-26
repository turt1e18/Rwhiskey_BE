package com.turt1e18.rwhiskey.rwhiskey.api.whiskey.entity

import jakarta.persistence.*

@Entity
@Table(name = "category_tags")
class CategoryTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(nullable = false)
    val label: String
)

@Entity
@Table(name = "flavor_tags")
class FlavorTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(nullable = false)
    val label: String
)

@Entity
@Table(name = "region_tags")
class RegionTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(nullable = false)
    val label: String
)

@Entity
@Table(name = "style_tags")
class StyleTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(nullable = false)
    val label: String
)
