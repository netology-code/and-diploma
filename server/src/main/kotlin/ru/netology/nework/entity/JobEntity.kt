package ru.netology.nework.entity

import ru.netology.nework.dto.Job
import javax.persistence.*

@Entity
data class JobEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @ManyToOne
    var user: UserEntity,
    /**
     * Название компании
     */
    @Column(columnDefinition = "TEXT")
    var name: String,
    @Column(columnDefinition = "TEXT")
    var position: String,
    /**
     * Дата и время начала работы
     */
    var start: Long,
    /**
     * Дата и время окончания работы
     */
    var finish: Long? = null,
    /**
     * Ссылка на веб-сайт организации
     */
    var link: String? = null,
) {
    fun toDto(myId: Long) = Job(id, name, position, start, finish, link)

    companion object {
        fun fromDto(dto: Job, myId: Long) = JobEntity(
            dto.id,
            UserEntity(myId),
            dto.name,
            dto.position,
            dto.start,
            dto.finish,
            dto.link,
        )
    }
}
