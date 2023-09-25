package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.service.PostService

@Tag(name = "Wall", description = "Посты одного юзера")
@RestController
@RequestMapping("/api/{authorId}/wall")
class WallController(private val service: PostService) {
    @GetMapping
    fun getAll(@PathVariable authorId: Long) = service.getAllByAuthorId(authorId)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @GetMapping("/{id:\\d+}")
    fun getById(@PathVariable authorId: Long, @PathVariable id: Long) = service.getByAuthorIdAndId(authorId, id)

    @GetMapping("/latest")
    fun getLatest(@PathVariable authorId: Long, @RequestParam count: Int) = service.getLatestByAuthorId(authorId, count)

    @GetMapping("/{id}/newer")
    fun getNewer(@PathVariable authorId: Long, @PathVariable id: Long) = service.getNewerByAuthorId(authorId, id)

    @GetMapping("/{id}/before")
    fun getBefore(@PathVariable authorId: Long, @PathVariable id: Long, @RequestParam count: Int) = service.getBeforeByAuthorId(authorId, id, count)

    @GetMapping("/{id}/after")
    fun getAfterByAuthorId(@PathVariable authorId: Long, @PathVariable id: Long, @RequestParam count: Int) = service.getAfterByAuthorId(authorId, id, count)

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