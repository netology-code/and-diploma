package ru.netology.nework.dto

import ru.netology.nework.enumeration.EventType

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    /**
     * Дата и время проведения
     */
    val datetime: Long,
    val published: Long,
    /**
     * Координаты проведения
     */
    val coords: Pair<Double, Double>? = null,
    /**
     * Типы события
     */
    val type: EventType,
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
)
