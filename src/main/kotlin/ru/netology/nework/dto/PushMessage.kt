package ru.netology.nework.dto

data class PushMessage(
    val recipientId: Long?,
    val content: String,
)
