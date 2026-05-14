package ru.netology.nework.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: Instant,
    val finish: Instant? = null,
    val link: String? = null,
) {
    @JsonCreator
    constructor(
        @JsonProperty("id") id: Long?,
        @JsonProperty("name") name: String,
        @JsonProperty("position") position: String,
        @JsonProperty("start") start: Instant,
        @JsonProperty("finish") finish: Instant?,
        @JsonProperty("link") link: String?,
    ) : this(
        id = id ?: 0L,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link,
    )
}
