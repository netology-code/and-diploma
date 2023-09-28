package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
    @Operation(security = [SecurityRequirement(name = "Api-Key")])
    fun getAll() = service.getAll()

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

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun save(@RequestBody dto: Event) = service.save(dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun removeById(@PathVariable id: Long) = service.removeById(id)

    @PostMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun likeById(@PathVariable id: Long) = service.likeById(id)

    @DeleteMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun unlikeById(@PathVariable id: Long) = service.unlikeById(id)

    @PostMapping("/{id}/participants")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun participateById(@PathVariable id: Long) = service.participateById(id)

    @DeleteMapping("/{id}/participants")
    @PreAuthorize("hasRole('USER')")
    @Operation(security = [SecurityRequirement(name = "Authorization"), SecurityRequirement(name = "Api-Key")])
    fun unparticipateById(@PathVariable id: Long) = service.unparticipateById(id)
}