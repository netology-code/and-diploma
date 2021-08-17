package ru.netology.nework.entity

import ru.netology.nework.dto.Post
import java.time.Instant
import javax.persistence.*

@Entity
data class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @ManyToOne
    var author: UserEntity,
    @Column(columnDefinition = "TEXT")
    var content: String,
    var published: Instant,
    /**
     * Координаты
     */
    @Embedded
    var coords: CoordinatesEmbeddable? = null,
    /**
     * Ссылка на связанный ресурс, например, событие (/events/{id}) или пользователя (/users/{id})
     */
    var link: String? = null,
    /**
     * Id'шники тех людей/компаний, которые упоминаются в посте (чтобы можно было перейти в их профили)
     */
    @ElementCollection
    var mentionIds: MutableSet<Long> = mutableSetOf(),
    /**
     * Id'шники залайкавших
     */
    @ElementCollection
    var likeOwnerIds: MutableSet<Long> = mutableSetOf(),
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
) {

    fun toDto(myId: Long) = Post(
        id = id,
        authorId = author.id,
        author = author.name,
        authorAvatar = author.avatar,
        content = content,
        published = published,
        coords = coords?.toCoordinates(),
        link = link,
        mentionIds = mentionIds,
        mentionedMe = mentionIds.contains(myId),
        likeOwnerIds = likeOwnerIds,
        likedByMe = likeOwnerIds.contains(myId),
        attachment = attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            id = dto.id,
            author = UserEntity(dto.authorId),
            content = dto.content,
            published = dto.published,
            coords = dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
            link = dto.link,
            mentionIds = dto.mentionIds.toMutableSet(),
            likeOwnerIds = mutableSetOf(),
            attachment = AttachmentEmbeddable.fromDto(dto.attachment),
        )
    }
}

