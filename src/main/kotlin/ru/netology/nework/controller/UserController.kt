package ru.netology.nework.controller

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.PushToken
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.service.UserService

@RestController
@RequestMapping("/api/users", "/api/slow/users")
class UserController(private val service: UserService) {
    @GetMapping
    fun getAll(): List<UserResponse> = service.getAll()

    @GetMapping("/{id:\\d+}")
    fun getById(@PathVariable id: Long): UserResponse = service.getById(id)

    @PostMapping("/registration")
    fun register(
        @RequestParam login: String,
        @RequestParam pass: String,
        @RequestParam name: String,
        @RequestParam(required = false) file: MultipartFile?,
    ): Token = service.register(login, pass, name, file)

    @PostMapping("/authentication")
    fun login(@RequestParam login: String, @RequestParam pass: String): Token =
        service.login(login, pass)

    @PostMapping("/push-tokens")
    fun saveToken(@RequestBody pushToken: PushToken): Unit = service.saveToken(pushToken)
}