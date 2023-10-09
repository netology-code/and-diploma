package ru.netology.nework.dto

import java.time.Instant

data class Comment(
    val id: Long = 0,
    val postId: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val content: String,
    val published: Instant = Instant.now(),
    val likeOwnerIds: Set<Long> = emptySet(),
)
