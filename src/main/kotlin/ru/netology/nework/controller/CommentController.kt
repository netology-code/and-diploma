package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.dto.Comment
import ru.netology.nework.service.CommentService

@Tag(name = "Comments")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
class CommentController(private val service: CommentService) {
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    @GetMapping
    fun getAllByPostId(@PathVariable postId: Long): List<Comment> = service.getAllByPostId(postId)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Пост не найден")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    @PostMapping
    fun save(@RequestBody dto: Comment, @PathVariable postId: Long): Comment =
        service.save(dto.copy(postId = postId))

    @ApiResponse(responseCode = "200")
    @ApiResponse(
        responseCode = "403",
        content = [Content()],
        description = "Нужно авторизоваться или удалять свой коммент"
    )
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): Unit = service.removeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Коммент не найден")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    @PostMapping("/{id}/likes")
    fun likeById(@PathVariable id: Long): Comment = service.likeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Коммент не найден")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    @DeleteMapping("/{id}/likes")
    fun unlikeById(@PathVariable id: Long): Comment = service.unlikeById(id)
}
