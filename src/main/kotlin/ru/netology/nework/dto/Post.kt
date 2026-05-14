package ru.netology.nework.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank
import ru.netology.nework.utils.InstantWithDefaultDeserializer
import java.time.Instant

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    @field:NotBlank
    val content: String,
    @field:JsonDeserialize(using = InstantWithDefaultDeserializer::class)
    val published: Instant,
    /**
     * Координаты
     */
    val coords: Coordinates? = null,
    /**
     * Ссылка на связанный ресурс, например:
     * 1. событие (/events/{id})
     * 2. пользователя (/users/{id})
     * 3. другой пост (/posts/{id})
     * 4. внешний контент (https://youtube.com и т.д.)
     * 5. и т.д.
     */
    val link: String? = null,
    /**
     * Id'шники тех людей/компаний, которые упоминаются в посте (чтобы можно было перейти в их профили)
     */
    val mentionIds: Set<Long>,
    /**
     * Упомянули ли меня
     */
    val mentionedMe: Boolean,
    /**
     * Id'шники залайкавших
     */
    val likeOwnerIds: Set<Long>,
    /**
     * Залайкал ли я
     */
    val likedByMe: Boolean,
    val attachment: Attachment? = null,
    /**
     * Все юзеры, участвующие в посте как упомянутые и лайкеры
     */
    val users: Map<Long, UserPreview>,
) {
    @JsonCreator
    constructor(
        @JsonProperty("id") id: Long?,
        @JsonProperty("authorId") authorId: Long?,
        @JsonProperty("author") author: String?,
        @JsonProperty("authorJob") authorJob: String?,
        @JsonProperty("authorAvatar") authorAvatar: String?,
        @JsonProperty("content") content: String,
        @JsonProperty("published") published: Instant?,
        @JsonProperty("coords") coords: Coordinates?,
        @JsonProperty("link") link: String?,
        @JsonProperty("mentionIds") mentionIds: Set<Long>?,
        @JsonProperty("mentionedMe") mentionedMe: Boolean?,
        @JsonProperty("likeOwnerIds") likeOwnerIds: Set<Long>?,
        @JsonProperty("likedByMe") likedByMe: Boolean?,
        @JsonProperty("attachment") attachment: Attachment?,
        @JsonProperty("users") users: Map<Long, UserPreview>?,
    ) : this(
        id = id ?: 0,
        authorId = authorId ?: 0,
        author = author ?: "",
        authorJob = authorJob,
        authorAvatar = authorAvatar,
        content = content,
        published = published ?: Instant.now(),
        coords = coords,
        link = link,
        mentionIds = mentionIds.orEmpty(),
        mentionedMe = mentionedMe ?: false,
        likeOwnerIds = likeOwnerIds.orEmpty(),
        likedByMe = likedByMe ?: false,
        attachment = attachment,
        users = users.orEmpty(),
    )
}
