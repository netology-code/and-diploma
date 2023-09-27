package ru.netology.nework.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Instant

@Converter(autoApply = true)
class InstantConverter : AttributeConverter<Instant, Long> {

    override fun convertToDatabaseColumn(attribute: Instant): Long =
        attribute.toEpochMilli()

    override fun convertToEntityAttribute(dbData: Long): Instant =
        Instant.ofEpochMilli(dbData)
}
