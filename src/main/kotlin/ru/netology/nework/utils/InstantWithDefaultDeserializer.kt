package ru.netology.nework.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import java.time.Instant


class InstantWithDefaultDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        return when (p.currentToken) {
            JsonToken.VALUE_STRING -> Instant.parse(p.valueAsString)
            JsonToken.VALUE_NULL -> Instant.now()
            else -> throw JsonMappingException.from(p, "Cannot deserialize Instant from token: ${p.currentToken}")
        }
    }
}
