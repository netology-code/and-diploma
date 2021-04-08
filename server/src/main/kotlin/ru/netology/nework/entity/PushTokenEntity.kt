package ru.netology.nework.entity

import javax.persistence.*

@Entity
data class PushTokenEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
    @Column(unique = true, nullable = false, updatable = false) var token: String,
    // for simplicity save just userId
    var userId: Long = 0,
)
