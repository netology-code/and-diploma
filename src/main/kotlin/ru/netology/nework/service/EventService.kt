package ru.netology.nework.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.netology.nework.dto.Event
import ru.netology.nework.entity.AttachmentEmbeddable
import ru.netology.nework.entity.CoordinatesEmbeddable
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PermissionDeniedException
import ru.netology.nework.extensions.principal
import ru.netology.nework.mapper.EventEntityToDtoMapper
import ru.netology.nework.repository.EventRepository
import java.time.Instant
import java.util.stream.Collectors

@Service
@Transactional
class EventService(
    private val repository: EventRepository,
    private val eventEntityToDtoMapper: EventEntityToDtoMapper,
) {
    companion object {
        const val maxLoadSize = 100
    }

    fun getAll(): List<Event> {
        return repository
            .findAll(Sort.by(Sort.Direction.DESC, "id"))
            .map { eventEntityToDtoMapper(it) }
    }

    fun getById(id: Long): Event {
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .let { eventEntityToDtoMapper(it) }
    }

    fun getLatest(count: Int): List<Event> {
        return repository
            .findAll(PageRequest.of(0, minOf(maxLoadSize, count), Sort.by(Sort.Direction.DESC, "id")))
            .content
            .map { eventEntityToDtoMapper(it) }
    }

    fun getNewer(id: Long): List<Event> {
        return repository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            .map { eventEntityToDtoMapper(it) }
            .collect(Collectors.toList())
    }

    fun getBefore(id: Long, count: Int): List<Event> {
        return repository
            .findAllByIdLessThan(id, Sort.by(Sort.Direction.DESC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { eventEntityToDtoMapper(it) }
            .collect(Collectors.toList())
    }

    fun getAfter(id: Long, count: Int): List<Event> {
        return repository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { eventEntityToDtoMapper(it) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun getAfterByAuthorId(authorId: Long, id: Long, count: Int): List<Event> {
        return repository
            .findAllByAuthorIdAndIdGreaterThan(authorId, id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { eventEntityToDtoMapper(it) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun save(dto: Event): Event {
        val principal = principal()
        return repository
            .findById(dto.id)
            .orElse(
                EventEntity.fromDto(
                    dto.copy(
                        authorId = principal.id,
                        author = principal.name,
                        authorAvatar = principal.avatar,
                        published = Instant.now()
                    )
                )
            )
            .let {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }

                it.copy(
                    type = dto.type,
                    datetime = dto.datetime,
                    content = dto.content,
                    coords = dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
                    speakerIds = dto.speakerIds.toMutableSet(),
                    attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                    link = dto.link,
                ).also(repository::save)
            }.let { eventEntityToDtoMapper(it) }
    }

    fun removeById(id: Long) {
        val principal = principal()
        repository.findById(id)
            .map {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }
                repository.delete(it)
                it
            }
    }

    fun likeById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .let {
                it.copy(likeOwnerIds = it.likeOwnerIds + principal.id)
            }
            .also(repository::save)
            .let { eventEntityToDtoMapper(it) }
    }

    fun unlikeById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .let {
                it.copy(likeOwnerIds = it.likeOwnerIds - principal.id)
            }
            .also(repository::save)
            .let { eventEntityToDtoMapper(it) }
    }

    fun participateById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                participantsIds.add(principal.id)
            }
            .let { eventEntityToDtoMapper(it) }
    }

    fun unparticipateById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                participantsIds.remove(principal.id)
            }
            .let { eventEntityToDtoMapper(it) }
    }
}