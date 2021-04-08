package ru.netology.nework.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PermissionDeniedException
import ru.netology.nework.extensions.principal
import ru.netology.nework.repository.PostRepository
import java.time.OffsetDateTime
import java.util.stream.Collectors

@Service
@Transactional
class PostService(
    private val repository: PostRepository,
) {
    companion object {
        const val maxLoadSize = 100
    }

    fun getAll(): List<Post> {
        val principal = principal()
        return repository
            .findAll(Sort.by(Sort.Direction.DESC, "id"))
            .map { it.toDto(principal.id) }
    }

    fun getAllMy(): List<Post> = getAllByAuthorId()

    fun getAllByAuthorId(authorId: Long = principal().id): List<Post> {
        val principal = principal()
        return repository
            .findAllByAuthorId(authorId, Sort.by(Sort.Direction.DESC, "id"))
            .map { it.toDto(principal.id) }
    }

    fun getById(id: Long): Post {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .toDto(principal.id)
    }

    fun getMyById(id: Long): Post = getByAuthorIdAndId(id = id)

    fun getByAuthorIdAndId(authorId: Long = principal().id, id: Long): Post {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .toDto(principal.id)
    }

    fun getLatest(count: Int): List<Post> {
        val principal = principal()
        return repository
            .findAll(PageRequest.of(0, minOf(maxLoadSize, count), Sort.by(Sort.Direction.DESC, "id")))
            .content
            .map { it.toDto(principal.id) }
    }

    fun getMyLatest(count: Int): List<Post> = getLatestByAuthorId(count = count)

    fun getLatestByAuthorId(authorId: Long = principal().id, count: Int): List<Post> {
        val principal = principal()
        return repository
            .findAllByAuthorId(
                authorId,
                PageRequest.of(0, minOf(maxLoadSize, count), Sort.by(Sort.Direction.DESC, "id"))
            )
            .content
            .map { it.toDto(principal.id) }
    }

    fun getNewer(id: Long): List<Post> {
        val principal = principal()
        return repository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
    }

    fun getMyNewer(id: Long): List<Post> = getNewerByAuthorId(id = id)

    fun getNewerByAuthorId(authorId: Long = principal().id, id: Long): List<Post> {
        val principal = principal()
        return repository
            .findAllByAuthorIdAndIdGreaterThan(authorId, id, Sort.by(Sort.Direction.ASC, "id"))
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
    }

    fun getBefore(id: Long, count: Int): List<Post> {
        val principal = principal()
        return repository
            .findAllByIdLessThan(id, Sort.by(Sort.Direction.DESC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
    }

    fun getMyBefore(id: Long, count: Int): List<Post> = getBeforeByAuthorId(id = id, count = count)

    fun getBeforeByAuthorId(authorId: Long = principal().id, id: Long, count: Int): List<Post> {
        val principal = principal()
        return repository
            .findAllByAuthorIdAndIdLessThan(authorId, id, Sort.by(Sort.Direction.DESC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
    }

    fun getAfter(id: Long, count: Int): List<Post> {
        val principal = principal()
        return repository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun getMyAfter(id: Long, count: Int): List<Post> = getAfterByAuthorId(id = id, count = count)

    fun getAfterByAuthorId(authorId: Long = principal().id, id: Long, count: Int): List<Post> {
        val principal = principal()
        return repository
            .findAllByAuthorIdAndIdGreaterThan(authorId, id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { it.toDto(principal.id) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun save(dto: Post): Post {
        val principal = principal()
        return repository
            .findById(dto.id)
            .orElse(
                PostEntity.fromDto(
                    dto.copy(
                        authorId = principal.id,
                        author = principal.name,
                        authorAvatar = principal.avatar,
                        likedByMe = false,
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
                    link = dto.link
                    mentionIds = dto.mentionIds.toMutableSet()
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

    fun likeById(id: Long): Post {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likeOwnerIds.add(principal.id)
            }
            .toDto(principal.id)
    }

    fun unlikeById(id: Long): Post {
        val principal = principal()
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likeOwnerIds.remove(principal.id)
            }
            .toDto(principal.id)
    }

    fun saveInitial(dto: Post) = PostEntity.fromDto(
        dto.copy(
            likedByMe = false,
            published = OffsetDateTime.now().toEpochSecond()
        )
    ).let(repository::save).toDto(0L)
}