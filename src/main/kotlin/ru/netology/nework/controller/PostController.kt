package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.dto.Post
import ru.netology.nework.service.PostService

@Tag(name = "Posts")
@RestController
@RequestMapping("/api/posts")
class PostController(private val service: PostService) {
    @GetMapping
    fun getAll() = service.getAll()

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @GetMapping("/{id:\\d+}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @GetMapping("/latest")
    fun getLatest(@RequestParam count: Int) = service.getLatest(count)

    @GetMapping("/{id}/newer")
    fun getNewer(@PathVariable id: Long) = service.getNewer(id)

    @GetMapping("/{id}/before")
    fun getBefore(@PathVariable id: Long, @RequestParam count: Int) = service.getBefore(id, count)

    @GetMapping("/{id}/after")
    fun getAfter(@PathVariable id: Long, @RequestParam count: Int) = service.getAfter(id, count)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun save(@RequestBody dto: Post) = service.save(dto)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться или удалять свой пост")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    fun removeById(@PathVariable id: Long) = service.removeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @PostMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    fun likeById(@PathVariable id: Long) = service.likeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @DeleteMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    fun unlikeById(@PathVariable id: Long) = service.unlikeById(id)

}