package ru.netology.nework.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import ru.netology.nework.dto.Post
import java.time.Instant

@Entity
data class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @ManyToOne
    val author: UserEntity,
    @Column(columnDefinition = "TEXT")
    val content: String,
    val published: Instant,
    /**
     * Координаты
     */
    @Embedded
    val coords: CoordinatesEmbeddable? = null,
    /**
     * Ссылка на связанный ресурс, например, событие (/events/{id}) или пользователя (/users/{id})
     */
    val link: String? = null,
    /**
     * Id'шники тех людей/компаний, которые упоминаются в посте (чтобы можно было перейти в их профили)
     */
    @ElementCollection
    val mentionIds: Set<Long> = setOf(),
    /**
     * Id'шники залайкавших
     */
    @ElementCollection
    val likeOwnerIds: Set<Long> = setOf(),
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    @OneToMany(
        mappedBy = "post",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val comments: List<CommentEntity> = emptyList(),
) {

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

