package ru.netology.nework.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nework.entity.UserEntity

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByLogin(login: String?): UserEntity?
}