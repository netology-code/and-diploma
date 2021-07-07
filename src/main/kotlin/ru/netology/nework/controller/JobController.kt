package ru.netology.nework.controller

import org.springframework.web.bind.annotation.*
import ru.netology.nework.service.JobService

@RestController
@RequestMapping("/api/{userId}/jobs", "/api/slow/{userId}/jobs")
class JobController(private val service: JobService) {
    @GetMapping
    fun getAll(@PathVariable userId: Long) = service.getAllByUserId(userId)
}