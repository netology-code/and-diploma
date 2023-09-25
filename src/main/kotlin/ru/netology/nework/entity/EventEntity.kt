package ru.netology.nework.entity

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import ru.netology.nework.dto.Event
import ru.netology.nework.enumeration.EventType
import java.time.Instant

@Entity
data class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @ManyToOne
    val author: UserEntity,
    @Column(columnDefinition = "TEXT")
    val content: String,
    /**
     * Дата и время проведения
     */
    val datetime: Instant,
    val published: Instant,
    /**
     * Координаты проведения
     */
    @Embedded
    val coords: CoordinatesEmbeddable? = null,
    /**
     * Типы события
     */
    @Column(name = "eventType")
    @Enumerated(EnumType.STRING)
    val type: EventType,
    @ElementCollection
    /**
     * Id'шники залайкавших
     */
    val likeOwnerIds: Set<Long> = emptySet(),
    /**
     * Id'шники спикеров
     */
    @ElementCollection
    val speakerIds: Set<Long> = emptySet(),
    /**
     * Id'шники участников
     */
    @ElementCollection
    val participantsIds: MutableSet<Long> = mutableSetOf(),
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val link: String? = null,
) {

    companion object {
        fun fromDto(dto: Event) = EventEntity(
            dto.id,
            UserEntity(dto.authorId),
            dto.content,
            dto.datetime,
            dto.published,
            dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
            dto.type,
            mutableSetOf(),
            dto.speakerIds.toMutableSet(),
            dto.participantsIds.toMutableSet(),
            AttachmentEmbeddable.fromDto(dto.attachment),
            dto.link,
        )
    }
}
