package ru.netology.nework.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class Comment(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Instant,
    val likeOwnerIds: Set<Long>,
    val likedByMe: Boolean,
) {
    @JsonCreator
    constructor(
        @JsonProperty("id") id: Long?,
        @JsonProperty("postId") postId: Long?,
        @JsonProperty("authorId") authorId: Long?,
        @JsonProperty("authorAvatar") authorAvatar: String?,
        @JsonProperty("author") author: String?,
        @JsonProperty("author") content: String,
        @JsonProperty("published") published: Instant?,
        @JsonProperty("likeOwnerIds") likeOwnerIds: Set<Long>?,
        @JsonProperty("likedByMe") likedByMe: Boolean?,
    ): this(
        id = id ?: 0L,
        postId = postId ?: 0L,
        authorId = authorId ?: 0L,
        author = author.orEmpty(),
        authorAvatar = authorAvatar,
        content = content,
        published = published ?: Instant.now(),
        likeOwnerIds = likeOwnerIds.orEmpty(),
        likedByMe = likedByMe ?: false,
    )
}
