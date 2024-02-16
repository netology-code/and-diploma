package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    @GetMapping
    fun getAll() = service.getAllMy()

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun save(@RequestBody dto: Job) = service.save(dto)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun removeById(@PathVariable id: Long) = service.removeById(id)
}