package ru.netology.nework.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nework.entity.JobEntity
import java.util.*
import java.util.stream.Stream

interface JobRepository : JpaRepository<JobEntity, Long> {
    fun findAllByUserId(userId: Long, sort: Sort): List<JobEntity>
    fun findAllByUserId(userId: Long, page: Pageable): Page<JobEntity>
    fun findAllByUserIdAndId(userId: Long, id: Long): Optional<JobEntity>
    fun findAllByUserIdAndIdLessThan(userId: Long, id: Long, sort: Sort): Stream<JobEntity>
    fun findAllByUserIdAndIdGreaterThan(userId: Long, id: Long, sort: Sort): Stream<JobEntity>
}