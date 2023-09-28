package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.service.PostService

@Tag(name = "MyWall", description = "Посты фильтруются по вашему токену авторизации")
@RestController
@RequestMapping("/api/my/wall")
class MyWallController(private val service: PostService) {
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    @GetMapping
    fun getAll() = service.getAllMy()

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @GetMapping("/{id:\\d+}")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getById(@PathVariable id: Long) = service.getMyById(id)

    @GetMapping("/latest")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getLatest(@RequestParam count: Int) = service.getMyLatest(count)

    @GetMapping("/{id}/newer")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getNewer(@PathVariable id: Long) = service.getMyNewer(id)

    @GetMapping("/{id}/before")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getBefore(@PathVariable id: Long, @RequestParam count: Int) = service.getMyBefore(id, count)

    @GetMapping("/{id}/after")
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getAfter(@PathVariable id: Long, @RequestParam count: Int) = service.getMyAfter(id, count)

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