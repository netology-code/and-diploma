package ru.netology.nework.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.AttachmentEmbeddable
import ru.netology.nework.entity.CoordinatesEmbeddable
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PermissionDeniedException
import ru.netology.nework.extensions.principal
import ru.netology.nework.mapper.PostEntityToDtoMapper
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.repository.UserRepository
import java.time.Instant
import java.util.stream.Collectors

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val postEntityToDtoMapper: PostEntityToDtoMapper,
) {
    companion object {
        const val maxLoadSize = 100
    }

    fun getAll(): List<Post> {
        return postRepository
            .findAll(Sort.by(Sort.Direction.DESC, "id"))
            .map { postEntityToDtoMapper(it) }
    }

    fun getAllMy(): List<Post> = getAllByAuthorId()

    fun getAllByAuthorId(authorId: Long = principal().id): List<Post> {
        return postRepository
            .findAllByAuthorId(authorId, Sort.by(Sort.Direction.DESC, "id"))
            .map { postEntityToDtoMapper(it) }
    }

    fun getById(id: Long): Post {
        return postRepository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .let { postEntityToDtoMapper(it) }
    }

    fun getMyById(id: Long): Post = getByAuthorIdAndId(id = id)

    fun getByAuthorIdAndId(authorId: Long = principal().id, id: Long): Post {
        return postRepository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .let { postEntityToDtoMapper(it) }
    }

    fun getLatest(count: Int): List<Post> {
        return postRepository
            .findAll(PageRequest.of(0, minOf(maxLoadSize, count), Sort.by(Sort.Direction.DESC, "id")))
            .content
            .map { postEntityToDtoMapper(it) }
    }

    fun getMyLatest(count: Int): List<Post> = getLatestByAuthorId(count = count)

    fun getLatestByAuthorId(authorId: Long = principal().id, count: Int): List<Post> {
        return postRepository
            .findAllByAuthorId(
                authorId,
                PageRequest.of(0, minOf(maxLoadSize, count), Sort.by(Sort.Direction.DESC, "id"))
            )
            .content
            .map { postEntityToDtoMapper(it) }
    }

    fun getNewer(id: Long): List<Post> {
        return postRepository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            .map { postEntityToDtoMapper(it) }
            .collect(Collectors.toList())
    }

    fun getMyNewer(id: Long): List<Post> = getNewerByAuthorId(id = id)

    fun getNewerByAuthorId(authorId: Long = principal().id, id: Long): List<Post> {
        return postRepository
            .findAllByAuthorIdAndIdGreaterThan(authorId, id, Sort.by(Sort.Direction.ASC, "id"))
            .map { postEntityToDtoMapper(it) }
            .collect(Collectors.toList())
    }

    fun getBefore(id: Long, count: Int): List<Post> {
        return postRepository
            .findAllByIdLessThan(id, Sort.by(Sort.Direction.DESC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { postEntityToDtoMapper(it) }
            .collect(Collectors.toList())
    }

    fun getMyBefore(id: Long, count: Int): List<Post> = getBeforeByAuthorId(id = id, count = count)

    fun getBeforeByAuthorId(authorId: Long = principal().id, id: Long, count: Int): List<Post> {
        return postRepository
            .findAllByAuthorIdAndIdLessThan(authorId, id, Sort.by(Sort.Direction.DESC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { postEntityToDtoMapper(it) }
            .collect(Collectors.toList())
    }

    fun getAfter(id: Long, count: Int): List<Post> {
        return postRepository
            .findAllByIdGreaterThan(id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { postEntityToDtoMapper(it) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun getMyAfter(id: Long, count: Int): List<Post> = getAfterByAuthorId(id = id, count = count)

    fun getAfterByAuthorId(authorId: Long = principal().id, id: Long, count: Int): List<Post> {
        return postRepository
            .findAllByAuthorIdAndIdGreaterThan(authorId, id, Sort.by(Sort.Direction.ASC, "id"))
            // just for simplicity: use normal limiting in production
            .limit(minOf(maxLoadSize, count).toLong())
            .map { postEntityToDtoMapper(it) }
            .collect(Collectors.toList())
            .reversed()
    }

    fun save(dto: Post): Post {
        val principal = principal()
        return postRepository
            .findById(dto.id)
            .orElse(
                PostEntity.fromDto(
                    dto.copy(
                        likedByMe = false,
                        published = Instant.now()
                    )
                ).copy(author = userRepository.getReferenceById(principal.id))
            )
            .let {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }

                val updated = it.copy(
                    content = dto.content,
                    coords = dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
                    link = dto.link,
                    mentionIds = dto.mentionIds.toMutableSet(),
                    attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                )

                postRepository.save(updated)

                updated
            }.let { postEntityToDtoMapper(it) }
    }

    fun removeById(id: Long) {
        val principal = principal()
        postRepository.findById(id)
            .orElseThrow(::NotFoundException)
            .let {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }
                postRepository.delete(it)
                it
            }
    }

    fun likeById(id: Long): Post {
        val principal = principal()
        return postRepository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likeOwnerIds.add(principal.id)
            }
            .let { postEntityToDtoMapper(it) }
    }

    fun unlikeById(id: Long): Post {
        val principal = principal()
        return postRepository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likeOwnerIds.remove(principal.id)
            }
            .let { postEntityToDtoMapper(it) }
    }
}