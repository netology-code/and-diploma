package ru.netology.nework.entity

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import ru.netology.nework.dto.Comment
import java.time.Instant

@Entity
data class CommentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    val post: PostEntity,
    @Column(columnDefinition = "TEXT")
    val content: String,
    val published: Instant,
    @ElementCollection
    val likeOwnerIds: Set<Long> = setOf(),
    @ManyToOne
    val author: UserEntity,
) {
    fun toDto() = Comment(
        id = id,
        postId = post.id,
        author = author.name,
        authorId = author.id,
        authorAvatar = author.avatar,
        content = content,
        published = published,
        likeOwnerIds = likeOwnerIds,
    )

    companion object {
        fun fromDto(dto: Comment, post: PostEntity) = CommentEntity(
            id = dto.id,
            post = post,
            author = UserEntity(dto.authorId),
            content = dto.content,
            published = dto.published,
            likeOwnerIds = dto.likeOwnerIds,
        )
    }
}
