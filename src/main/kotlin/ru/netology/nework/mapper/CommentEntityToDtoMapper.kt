package ru.netology.nework.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.netology.nework.dto.Comment
import ru.netology.nework.entity.CommentEntity
import ru.netology.nework.extensions.principalOrNull

@Component
class CommentEntityToDtoMapper @Autowired constructor() {
    operator fun invoke(commentEntity: CommentEntity): Comment = with(commentEntity) {
        val principal = principalOrNull()

        val myId = principal?.id

        Comment(
            id = id,
            authorId = author.id,
            author = author.name,
            authorAvatar = author.avatar,
            content = content,
            published = published,
            likeOwnerIds = likeOwnerIds,
            likedByMe = likeOwnerIds.contains(myId),
            postId = post.id,
        )
    }
}