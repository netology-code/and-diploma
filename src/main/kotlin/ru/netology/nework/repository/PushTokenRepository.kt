package ru.netology.nework.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nework.entity.PushTokenEntity
import java.util.*

interface PushTokenRepository : JpaRepository<PushTokenEntity, Long> {
    fun findByToken(token: String?): Optional<PushTokenEntity>
}