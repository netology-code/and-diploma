package ru.netology.nework.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.extensions.principalOrNull
import ru.netology.nework.repository.JobRepository
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.utils.getOrNull

@Component
class PostEntityToDtoMapper @Autowired constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
) {
    operator fun invoke(postEntity: PostEntity): Post = with(postEntity) {
        val principal = principalOrNull()

        val myId = principal?.id

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
            authorJob = jobRepository.findFirstByUserIdOrderByStartDesc(author.id)
                .getOrNull()?.name,
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