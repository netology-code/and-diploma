package ru.netology.nework.extensions

import org.springframework.security.core.context.SecurityContextHolder
import ru.netology.nework.dto.User

fun principal() =
    requireNotNull(SecurityContextHolder.getContext().authentication).principal as User

fun principalOrNull() = SecurityContextHolder.getContext().authentication?.principal as? User?
