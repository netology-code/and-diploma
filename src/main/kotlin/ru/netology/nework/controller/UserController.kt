package ru.netology.nework.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.service.UserService

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
class UserController(private val service: UserService) {
    @GetMapping
    fun getAll(): List<UserResponse> = service.getAll()

    @GetMapping("/{id:\\d+}")
    fun getById(@PathVariable id: Long): UserResponse = service.getById(id)

    @PostMapping(
        "/registration",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun register(
        @RequestParam login: String,
        @RequestParam pass: String,
        @RequestParam name: String,
        @RequestParam(required = false) file: MultipartFile?,
    ): Token = service.register(login, pass, name, file)

    @PostMapping("/authentication")
    fun login(@RequestParam login: String, @RequestParam pass: String): Token =
        service.login(login, pass)
}