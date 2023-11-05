package ru.netology.nework.dto

import jakarta.validation.constraints.NotBlank
import ru.netology.nework.enumeration.EventType
import java.time.Instant

data class Event(
    val id: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    @NotBlank
    val content: String,
    /**
     * Дата и время проведения
     */
    val datetime: Instant,
    val published: Instant = Instant.now(),
    /**
     * Координаты проведения
     */
    val coords: Coordinates? = null,
    /**
     * Типы события
     */
    val type: EventType = EventType.ONLINE,
    /**
     * Id'шники залайкавших
     */
    val likeOwnerIds: Set<Long> = emptySet(),
    /**
     * Залайкал ли я
     */
    val likedByMe: Boolean = false,
    /**
     * Id'шники спикеров
     */
    val speakerIds: Set<Long> = emptySet(),
    /**
     * Id'шники участников
     */
    val participantsIds: Set<Long> = emptySet(),
    /**
     * Участвовал ли я
     */
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,

    /**
     * Все юзеры, участвующие в посте как участники, спикеры и лайкеры
     */
    val users: Map<Long, UserPreview> = emptyMap(),
)
