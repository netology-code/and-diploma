package ru.netology.nework.repository

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nework.entity.EventEntity
import java.util.stream.Stream

interface EventRepository : JpaRepository<EventEntity, Long> {
    fun findAllByIdLessThan(id: Long, sort: Sort): Stream<EventEntity>
    fun findAllByIdGreaterThan(id: Long, sort: Sort): Stream<EventEntity>
    fun findAllByAuthorIdAndIdGreaterThan(authorId: Long, id: Long, sort: Sort): Stream<EventEntity>
}