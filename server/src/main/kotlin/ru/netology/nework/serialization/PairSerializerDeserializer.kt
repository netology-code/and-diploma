package ru.netology.nework.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*

object CoordsSerializer : JsonSerializer<Pair<Double, Double>>() {
    override fun serialize(value: Pair<Double, Double>, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeArray(arrayOf(value.first, value.second).toDoubleArray(), 0, 2)
    }
}

object CoordsDeserializer : JsonDeserializer<Pair<Double, Double>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Pair<Double, Double> {
        val node = p.readValueAsTree<JsonNode>()
        val lat = node.get(0).asDouble()
        val lon = node.get(1).asDouble()
        return lat to lon
    }
}