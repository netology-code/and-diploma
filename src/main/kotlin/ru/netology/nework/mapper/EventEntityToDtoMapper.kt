package ru.netology.nework.mapper

import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.extensions.principal
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.utils.getOrNull

class EventEntityToDtoMapper(
    private val userRepository: UserRepository,
) {
    operator fun invoke(eventEntity: EventEntity): Event = with(eventEntity) {
        val principal = principal()

        val myId = principal.id

        val allUsers = participantsIds + likeOwnerIds + speakerIds

        val users = allUsers.mapNotNull { id ->
            userRepository.findById(id).getOrNull()
                ?.let {
                    id to it.toPreview()
                }
        }
            .toMap()

        Event(
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
            attachment = attachment?.toDto(),
            link = link,
            users = users,
        )
    }
}