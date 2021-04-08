package ru.netology.nework.entity

import ru.netology.nework.dto.Event
import ru.netology.nework.enumeration.EventType
import javax.persistence.*

@Entity
data class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @ManyToOne
    var author: UserEntity,
    @Column(columnDefinition = "TEXT")
    var content: String,
    /**
     * Дата и время проведения
     */
    var datetime: Long,
    var published: Long,
    /**
     * Координаты проведения
     */
    var coords: Pair<Double, Double>? = null,
    /**
     * Типы события
     */
    @Column(name = "eventType")
    @Enumerated(EnumType.STRING)
    var type: EventType,
    @ElementCollection
    /**
     * Id'шники залайкавших
     */
    var likeOwnerIds: MutableSet<Long> = mutableSetOf(),
    /**
     * Id'шники спикеров
     */
    @ElementCollection
    var speakerIds: MutableSet<Long> = mutableSetOf(),
    /**
     * Id'шники участников
     */
    @ElementCollection
    var participantsIds: MutableSet<Long> = mutableSetOf(),
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
) {
    fun toDto(myId: Long) = Event(id, author.id, author.name, author.avatar, content, datetime, published, coords, type, likeOwnerIds, likeOwnerIds.contains(myId), speakerIds, participantsIds, participantsIds.contains(myId), attachment?.toDto())

    companion object {
        fun fromDto(dto: Event) = EventEntity(
            dto.id,
            UserEntity(dto.authorId),
            dto.content,
            dto.datetime,
            dto.published,
            dto.coords,
            dto.type,
            mutableSetOf(),
            dto.speakerIds.toMutableSet(),
            dto.participantsIds.toMutableSet(),
            AttachmentEmbeddable.fromDto(dto.attachment)
        )
    }
}
