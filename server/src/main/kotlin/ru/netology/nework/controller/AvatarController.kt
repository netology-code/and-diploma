package ru.netology.nework.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.service.MediaService

@RestController
@RequestMapping("/api/avatars", "/api/slow/avatars")
class AvatarController(private val service: MediaService) {
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun save(@RequestParam file: MultipartFile) = service.saveAvatar(file)
}