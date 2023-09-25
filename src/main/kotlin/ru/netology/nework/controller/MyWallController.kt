package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.service.PostService

@Tag(name = "MyWall", description = "Посты фильтруются по вашему токену авторизации")
@RestController
@RequestMapping("/api/my/wall")
class MyWallController(private val service: PostService) {
    @GetMapping
    fun getAll() = service.getAllMy()

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @GetMapping("/{id:\\d+}")
    fun getById(@PathVariable id: Long) = service.getMyById(id)

    @GetMapping("/latest")
    fun getLatest(@RequestParam count: Int) = service.getMyLatest(count)

    @GetMapping("/{id}/newer")
    fun getNewer(@PathVariable id: Long) = service.getMyNewer(id)

    @GetMapping("/{id}/before")
    fun getBefore(@PathVariable id: Long, @RequestParam count: Int) = service.getMyBefore(id, count)

    @GetMapping("/{id}/after")
    fun getAfter(@PathVariable id: Long, @RequestParam count: Int) = service.getMyAfter(id, count)

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