package ru.netology.nework.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.netology.nework.dto.Event
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PermissionDeniedException
import ru.netology.nework.extensions.principal
import ru.netology.nework.repository.EventRepository
import java.time.OffsetDateTime
import java.util.stream.Collectors

@Service
@Transactional
class EventService(
    private val repository: EventRepository,
) {
    companion object {
        const val maxLoadSize = 100
    }

    fun getAll(): List<Event> {
        val principal = principal()
        return repository
            .findAll(Sort.by(Sort.Direction.DESC, "id"))
            .map { it.toDto(principal.id) }
    }

    fun getById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .toDto(principal.id)
    }

    fun getLatest(count: Int): List<Event> {
        val principal = principal()
        return repository
            .findAll(PageRequest.of(0, minOf(maxLoadSize, count), Sort.by(Sort.Direction.DESC, "id")))
            .content
            .map { it.toDto(principal.id) }
    }

    fun getNewer(id: Long): List<Event> {
        val principal = principal()
        return repository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
    }

    fun getBefore(id: Long, count: Int): List<Event> {
        val principal = principal()
        return repository
            .findAllByIdLessThan(id, Sort.by(Sort.Direction.DESC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
    }

    fun getAfter(id: Long, count: Int): List<Event> {
        val principal = principal()
        return repository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun getAfterByAuthorId(authorId: Long, id: Long, count: Int): List<Event> {
        val principal = principal()
        return repository
            .findAllByAuthorIdAndIdGreaterThan(authorId, id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
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
                        published = OffsetDateTime.now().toEpochSecond()
                    )
                )
            )
            .let {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }

                it.run {
                    content = dto.content
                    coords = dto.coords
                    speakerIds = dto.speakerIds.toMutableSet()
                }
                if (it.id == 0L) repository.save(it)
                it
            }.toDto(principal.id)
    }

    fun removeById(id: Long): Unit {
        val principal = principal()
        repository.findById(id)
            .orElseThrow(::NotFoundException)
            .let {
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
            .apply {
                likeOwnerIds.add(principal.id)
            }
            .toDto(principal.id)
    }

    fun unlikeById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likeOwnerIds.remove(principal.id)
            }
            .toDto(principal.id)
    }

    fun participateById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                participantsIds.add(principal.id)
            }
            .toDto(principal.id)
    }

    fun unparticipateById(id: Long): Event {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                participantsIds.remove(principal.id)
            }
            .toDto(principal.id)
    }

    fun saveInitial(dto: Event) = EventEntity.fromDto(
        dto.copy(
            likedByMe = false,
            published = OffsetDateTime.now().toEpochSecond()
        )
    ).let(repository::save).toDto(0L)
}