package ru.netology.nework.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.utils.InstantWithDefaultDeserializer
import java.time.Instant
import java.time.temporal.ChronoUnit

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    @field:NotBlank
    val content: String,
    /**
     * Дата и время проведения
     */
    @field:JsonDeserialize(using = InstantWithDefaultDeserializer::class)
    val datetime: Instant,
    @field:JsonDeserialize(using = InstantWithDefaultDeserializer::class)
    val published: Instant,
    /**
     * Координаты проведения
     */
    val coords: Coordinates? = null,
    /**
     * Типы события
     */
    val type: EventType,
    /**
     * Id'шники залайкавших
     */
    val likeOwnerIds: Set<Long>,
    /**
     * Залайкал ли я
     */
    val likedByMe: Boolean,
    /**
     * Id'шники спикеров
     */
    val speakerIds: Set<Long>,
    /**
     * Id'шники участников
     */
    val participantsIds: Set<Long>,
    /**
     * Участвовал ли я
     */
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val link: String? = null,

    /**
     * Все юзеры, участвующие в посте как участники, спикеры и лайкеры
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
        @JsonProperty("datetime") datetime: Instant?,
        @JsonProperty("published") published: Instant?,
        @JsonProperty("coords") coords: Coordinates?,
        @JsonProperty("type") type: EventType?,
        @JsonProperty("likeOwnerIds") likeOwnerIds: Set<Long>?,
        @JsonProperty("likedByMe") likedByMe: Boolean?,
        @JsonProperty("speakerIds") speakerIds: Set<Long>?,
        @JsonProperty("participantsIds") participantsIds: Set<Long>?,
        @JsonProperty("participatedByMe") participatedByMe: Boolean?,
        @JsonProperty("attachment") attachment: Attachment?,
        @JsonProperty("link") link: String?,
        @JsonProperty("users") users: Map<Long, UserPreview>?,
    ) : this(
        id = id ?: 0,
        authorId = authorId ?: 0,
        author = author ?: "",
        authorJob = authorJob,
        authorAvatar = authorAvatar,
        content = content,
        datetime = datetime ?: Instant.now().plus(1, ChronoUnit.DAYS),
        published = published ?: Instant.now(),
        coords = coords,
        type = type ?: EventType.ONLINE,
        likeOwnerIds = likeOwnerIds.orEmpty(),
        likedByMe = likedByMe ?: false,
        speakerIds = speakerIds.orEmpty(),
        participantsIds = participantsIds.orEmpty(),
        participatedByMe = participatedByMe ?: false,
        attachment = attachment,
        link = link,
        users = users.orEmpty(),
    )
}
