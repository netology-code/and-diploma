package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.dto.Post
import ru.netology.nework.service.PostService

@Tag(name = "Posts")
@RestController
@RequestMapping("/api/posts")
class PostController(private val service: PostService) {
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    @GetMapping
    fun getAll() = service.getAll()

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @GetMapping("/{id:\\d+}")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getById(@PathVariable id: Long) = service.getById(id)

    @GetMapping("/latest")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getLatest(@RequestParam count: Int) = service.getLatest(count)

    @GetMapping("/{id}/newer")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getNewer(@PathVariable id: Long) = service.getNewer(id)

    @GetMapping("/{id}/before")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getBefore(@PathVariable id: Long, @RequestParam count: Int) = service.getBefore(id, count)

    @GetMapping("/{id}/after")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getAfter(@PathVariable id: Long, @RequestParam count: Int) = service.getAfter(id, count)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun save(@RequestBody dto: Post) = service.save(dto)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться или удалять свой пост")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun removeById(@PathVariable id: Long) = service.removeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @PostMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun likeById(@PathVariable id: Long) = service.likeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @DeleteMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun unlikeById(@PathVariable id: Long) = service.unlikeById(id)

}