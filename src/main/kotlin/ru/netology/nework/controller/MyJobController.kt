package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.service.JobService

@Tag(name = "MyJob")
@RestController
@RequestMapping("/api/my/jobs")
class MyJobController(private val service: JobService) {
    @GetMapping
    fun getAll() = service.getAllMy()

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun save(@RequestBody dto: Job) = service.save(dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    fun removeById(@PathVariable id: Long) = service.removeById(id)
}