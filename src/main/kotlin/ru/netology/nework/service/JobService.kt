package ru.netology.nework.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.netology.nework.dto.Job
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PermissionDeniedException
import ru.netology.nework.extensions.principal
import ru.netology.nework.repository.JobRepository

@Service
@Transactional
class JobService(
    private val repository: JobRepository,
) {
    companion object {
        const val maxLoadSize = 100
    }

    fun getAllMy(): List<Job> = getAllByUserId(principal().id)

    fun getAllByUserId(userId: Long = principal().id): List<Job> {
        val principal = principal()
        return repository
            .findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "start"))
            .map { it.toDto(principal.id) }
    }

    fun save(dto: Job): Job {
        val principal = principal()
        return repository
            .findById(dto.id)
            .ifPresent {
                if (it.user.id != principal.id) {
                    throw PermissionDeniedException()
                }
            }
            .let { JobEntity.fromDto(dto, principal.id) }
            .let(repository::save)
            .toDto(principal.id)
    }

    fun removeById(id: Long): Unit {
        val principal = principal()
        repository.findById(id)
            .orElseThrow(::NotFoundException)
            .let {
                if (it.user.id != principal.id) {
                    throw PermissionDeniedException()
                }
                repository.delete(it)
                it
            }
    }
}