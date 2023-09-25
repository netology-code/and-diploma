package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.service.MediaService

@Tag(name = "Media")
@RestController
@RequestMapping("/api/media")
class MediaController(private val service: MediaService) {
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", content = [Content()], description = "Нужно авторизоваться")
    @ApiResponse(responseCode = "415", content = [Content()], description = "Неправильный формат файла")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun save(@RequestParam file: MultipartFile) = service.save(file)
}