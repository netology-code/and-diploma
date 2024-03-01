package ru.netology.nework.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.netology.nework.dto.Event
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.extensions.principalOrNull
import ru.netology.nework.repository.JobRepository
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.utils.getOrNull

@Component
class EventEntityToDtoMapper @Autowired constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
) {
    operator fun invoke(eventEntity: EventEntity): Event = with(eventEntity) {
        val principal = principalOrNull()

        val myId = principal?.id

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
            authorJob = jobRepository.findFirstByUserIdOrderByStartDesc(author.id)
                .getOrNull()?.name,
        )
    }
}