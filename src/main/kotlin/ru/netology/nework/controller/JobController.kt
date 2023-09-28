package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import ru.netology.nework.service.JobService

@Tag(name = "Jobs")
@RestController
@RequestMapping("/api/{userId}/jobs")
class JobController(private val service: JobService) {
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    @GetMapping
    fun getAll(@PathVariable userId: Long) = service.getAllByUserId(userId)
}