package ru.netology.nework.entity

import ru.netology.nework.dto.Post
import javax.persistence.*

@Entity
data class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @ManyToOne
    var author: UserEntity,
    @Column(columnDefinition = "TEXT")
    var content: String,
    var published: Long,
    /**
     * Координаты
     */
    var coords: Pair<Double, Double>? = null,
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
    constructor(id: Long) : this(id, UserEntity(0), "", 0L)

    fun toDto(myId: Long) = Post(
        id,
        author.id,
        author.name,
        author.avatar,
        content,
        published,
        coords,
        link,
        mentionIds,
        mentionIds.contains(myId),
        likeOwnerIds,
        likeOwnerIds.contains(myId),
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            dto.id,
            UserEntity(dto.authorId),
            dto.content,
            dto.published,
            dto.coords,
            dto.link,
            dto.mentionIds.toMutableSet(),
            mutableSetOf(),
            AttachmentEmbeddable.fromDto(dto.attachment),
        )
    }
}

