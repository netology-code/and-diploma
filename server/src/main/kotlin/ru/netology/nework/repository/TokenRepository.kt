package ru.netology.nework.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nework.entity.TokenEntity

interface TokenRepository : JpaRepository<TokenEntity, String>