package ru.netology.nework.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.service.PostService

@RestController
@RequestMapping("/api/{authorId}/wall", "/api/slow/{authorId}/wall")
class WallController(private val service: PostService) {
    @GetMapping
    fun getAll(@PathVariable authorId: Long) = service.getAllByAuthorId(authorId)

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

    @PostMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    fun likeById(@PathVariable id: Long) = service.likeById(id)

    @DeleteMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    fun unlikeById(@PathVariable id: Long) = service.unlikeById(id)
}