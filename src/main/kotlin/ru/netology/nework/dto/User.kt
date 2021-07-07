package ru.netology.nework.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String,
    val authorities: List<String>,
)

val AnonymousUser = User(
    id = 0L,
    login = "anonymous",
    name = "Anonymous",
    avatar = "",
    authorities = listOf("ROLE_ANONYMOUS")
)
