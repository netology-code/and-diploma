package ru.netology.nework.entity

import jakarta.persistence.Embeddable
import ru.netology.nework.dto.Coordinates

@Embeddable
data class CoordinatesEmbeddable(
    val lat: Double? = null,
    val long: Double? = null,
) {

    fun toCoordinates(): Coordinates = Coordinates(lat = lat ?: 0.0, long = long ?: 0.0)

    companion object {
        fun fromCoordinates(coordinates: Coordinates): CoordinatesEmbeddable =
            with(coordinates) {
                CoordinatesEmbeddable(lat = lat, long = long)
            }
    }
}