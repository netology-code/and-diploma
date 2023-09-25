package ru.netology.nework.dto

import java.time.Instant

data class Job(
    val id: Long = 0,
    val name: String,
    val position: String,
    val start: Instant,
    val finish: Instant? = null,
    val link: String? = null,
)
