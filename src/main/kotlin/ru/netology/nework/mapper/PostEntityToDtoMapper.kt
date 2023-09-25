package ru.netology.nework.mapper

import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.extensions.principal
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.utils.getOrNull

class PostEntityToDtoMapper(
    private val userRepository: UserRepository,
    ) {
    operator fun invoke(postEntity: PostEntity): Post = with(postEntity) {
        val principal = principal()

        val myId = principal.id

        val allUsers = mentionIds + likeOwnerIds

        val users = allUsers.mapNotNull { id ->
            userRepository.findById(id).getOrNull()
                ?.let {
                    id to it.toPreview()
                }
        }
            .toMap()

        Post(
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
            attachment = attachment?.toDto(),
            users = users,
        )
    }
}