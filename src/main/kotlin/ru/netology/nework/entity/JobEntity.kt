package ru.netology.nework.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import ru.netology.nework.dto.Job
import java.time.Instant

@Entity
data class JobEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @ManyToOne
    val user: UserEntity,
    /**
     * Название компании
     */
    @Column(columnDefinition = "TEXT")
    val name: String,
    @Column(columnDefinition = "TEXT")
    val position: String,
    /**
     * Дата и время начала работы
     */
    val start: Instant,
    /**
     * Дата и время окончания работы
     */
    val finish: Instant? = null,
    /**
     * Ссылка на веб-сайт организации
     */
    val link: String? = null,
) {
    fun toDto() = Job(
        id = id,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link
    )

    companion object {
        fun fromDto(dto: Job, myId: Long) = JobEntity(
            id = dto.id,
            user = UserEntity(myId),
            name = dto.name,
            position = dto.position,
            start = dto.start,
            finish = dto.finish,
            link = dto.link,
        )
    }
}
