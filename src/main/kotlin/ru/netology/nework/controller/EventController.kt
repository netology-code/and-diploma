package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.netology.nework.dto.Event
import ru.netology.nework.service.EventService

@Tag(name = "Events")
@RestController
@RequestMapping("/api/events")
class EventController(private val service: EventService) {
    @GetMapping
    fun getAll() = service.getAll()

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
    @ApiResponse(responseCode = "404", content = [Content()], description = "Событие не найдено")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun save(@RequestBody dto: Event) = service.save(dto)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться или удалять своё событие")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    fun removeById(@PathVariable id: Long) = service.removeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Событие не найдено")
    @PostMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    fun likeById(@PathVariable id: Long) = service.likeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Событие не найдено")
    @DeleteMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    fun unlikeById(@PathVariable id: Long) = service.unlikeById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Событие не найдено")
    @PostMapping("/{id}/participants")
    @PreAuthorize("hasRole('USER')")
    fun participateById(@PathVariable id: Long) = service.participateById(id)

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "404", content = [Content()], description = "Событие не найдено")
    @DeleteMapping("/{id}/participants")
    @PreAuthorize("hasRole('USER')")
    fun unparticipateById(@PathVariable id: Long) = service.unparticipateById(id)
}