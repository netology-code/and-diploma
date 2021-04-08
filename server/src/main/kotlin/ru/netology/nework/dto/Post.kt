package ru.netology.nework.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.google.firestore.v1.DocumentOrBuilder
import ru.netology.nework.serialization.CoordsDeserializer
import ru.netology.nework.serialization.CoordsSerializer

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    /**
     * Координаты
     */
    @JsonSerialize(using = CoordsSerializer::class)
    @JsonDeserialize(using = CoordsDeserializer::class)
    val coords: Pair<Double, Double>? = null,
    /**
     * Ссылка на связанный ресурс, например:
     * 1. событие (/events/{id})
     * 2. пользователя (/users/{id})
     * 3. другой пост (/posts/{id})
     * 4. внешний контент (https://youtube.com и т.д.)
     * 5. и т.д.
     */
    val link: String? = null,
    /**
     * Id'шники тех людей/компаний, которые упоминаются в посте (чтобы можно было перейти в их профили)
     */
    val mentionIds: Set<Long> = emptySet(),
    /**
     * Упомянули ли меня
     */
    val mentionedMe: Boolean = false,
    /**
     * Id'шники залайкавших
     */
    val likeOwnerIds: Set<Long> = emptySet(),
    /**
     * Залайкал ли я
     */
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
)
