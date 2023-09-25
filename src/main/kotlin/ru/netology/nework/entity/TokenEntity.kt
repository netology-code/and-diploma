package ru.netology.nework.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
data class TokenEntity(
    @Id val token: String,
    @ManyToOne
    val user: UserEntity,
)
