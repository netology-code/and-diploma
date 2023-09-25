package ru.netology.nework.entity

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import ru.netology.nework.dto.Attachment
import ru.netology.nework.enumeration.AttachmentType

@Embeddable
data class AttachmentEmbeddable(
    val url: String,
    @Enumerated(EnumType.STRING)
    val type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}
