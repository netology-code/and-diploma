package ru.netology.nework.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nework.entity.PostEntity
import java.util.*
import java.util.stream.Stream

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findAllByIdLessThan(id: Long, sort: Sort): Stream<PostEntity>
    fun findAllByIdGreaterThan(id: Long, sort: Sort): Stream<PostEntity>
    fun findAllByAuthorId(authorId: Long, sort: Sort): List<PostEntity>
    fun findAllByAuthorId(authorId: Long, page: Pageable): Page<PostEntity>
    fun findByAuthorIdAndId(authorId: Long, id: Long): Optional<PostEntity>
    fun findAllByAuthorIdAndIdLessThan(authorId: Long, id: Long, sort: Sort): Stream<PostEntity>
    fun findAllByAuthorIdAndIdGreaterThan(authorId: Long, id: Long, sort: Sort): Stream<PostEntity>
}