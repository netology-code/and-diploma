package ru.netology.nework.entity

import java.time.Instant
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class InstantConverter : AttributeConverter<Instant, Long> {

    override fun convertToDatabaseColumn(attribute: Instant): Long =
        attribute.toEpochMilli()

    override fun convertToEntityAttribute(dbData: Long): Instant =
        Instant.ofEpochMilli(dbData)
}
