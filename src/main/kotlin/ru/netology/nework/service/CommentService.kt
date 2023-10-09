package ru.netology.nework.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.netology.nework.dto.Comment
import ru.netology.nework.entity.CommentEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PermissionDeniedException
import ru.netology.nework.extensions.principal
import ru.netology.nework.repository.CommentRepository
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.utils.getOrNull
import java.time.Instant

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {
    fun getAllByPostId(postId: Long): List<Comment> = commentRepository
        .findAllByPostId(postId, Sort.by(Sort.Direction.ASC, "id"))
        .map { it.toDto() }

    fun getById(id: Long): Comment = commentRepository
        .findById(id)
        .map { it.toDto() }
        .orElseThrow(::NotFoundException)

    fun save(dto: Comment): Comment {
        val principal = principal()
        return commentRepository
            .findById(dto.id)
            .orElse(
                CommentEntity.fromDto(
                    dto.copy(
                        published = Instant.now()
                    ),
                    post = postRepository.getReferenceById(dto.postId)
                ).copy(author = userRepository.getReferenceById(principal.id))
            )
            .let {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }

                val entity = if (it.id == 0L) it else it.copy(content = dto.content)
                commentRepository.save(entity)
                entity
            }.toDto()
    }

    fun removeById(id: Long) {
        val principal = principal()
        commentRepository.findById(id)
            .getOrNull()
            ?.let {
                if (it.author.id != principal.id) {
                    throw PermissionDeniedException()
                }
                commentRepository.deleteById(it.id)
            }
    }

    fun likeById(id: Long): Comment {
        val principal = principal()
        return commentRepository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .run {
                copy(likeOwnerIds = likeOwnerIds + principal.id)
            }
            .also(commentRepository::save)
            .toDto()
    }

    fun unlikeById(id: Long): Comment {
        val principal = principal()
        return commentRepository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .run {
                copy(likeOwnerIds = likeOwnerIds - principal.id)
            }
            .also(commentRepository::save)
            .toDto()
    }

    fun removeAllByPostId(postId: Long): Unit = commentRepository
        .removeAllByPostId(postId)
}