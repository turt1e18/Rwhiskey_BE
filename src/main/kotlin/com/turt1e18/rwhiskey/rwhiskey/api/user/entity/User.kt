package com.turt1e18.rwhiskey.rwhiskey.api.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    var uid: Int? = null,

    @Column(name="email",nullable = false, unique = true)
    var email: String,

    @Column(name="pw",nullable = false)
    var pw: String,

    @Column(name="name",nullable = false)
    var name: String,

    @Column(name="type",nullable = false)
    var type: Int = 0,

    @Column(name="create_date")
    var createDate: LocalDateTime = LocalDateTime.now(),

    @Column(name="delete_date")
    var deleteDate: LocalDateTime? = null
)
