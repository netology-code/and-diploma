package ru.netology.nework.entity

import ru.netology.nework.dto.Event
import ru.netology.nework.enumeration.EventType
import java.time.Instant
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
    var datetime: Instant,
    var published: Instant,
    /**
     * Координаты проведения
     */
    @Embedded
    var coords: CoordinatesEmbeddable? = null,
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
    fun toDto(myId: Long) = Event(
        id = id,
        authorId = author.id,
        author = author.name,
        authorAvatar = author.avatar,
        content = content,
        datetime = datetime,
        published = published,
        coords = coords?.toCoordinates(),
        type = type,
        likeOwnerIds = likeOwnerIds,
        likedByMe = likeOwnerIds.contains(myId),
        speakerIds = speakerIds,
        participantsIds = participantsIds,
        participatedByMe = participantsIds.contains(myId),
        attachment = attachment?.toDto()
    )

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
            AttachmentEmbeddable.fromDto(dto.attachment)
        )
    }
}
