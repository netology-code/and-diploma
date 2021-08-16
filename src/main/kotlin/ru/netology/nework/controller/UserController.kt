package ru.netology.nework.controller

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.PushToken
import ru.netology.nework.dto.User
import ru.netology.nework.service.UserService

@RestController
@RequestMapping("/api/users", "/api/slow/users")
class UserController(private val service: UserService) {
    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id:\\d+}")
    fun getById(@PathVariable id: Long): User = service.getById(id)

    @PostMapping("/registration")
    fun register(
        @RequestParam login: String,
        @RequestParam pass: String,
        @RequestParam name: String,
        @RequestParam(required = false) file: MultipartFile?,
    ) = service.register(login, pass, name, file)

    @PostMapping("/authentication")
    fun login(@RequestParam login: String, @RequestParam pass: String) = service.login(login, pass)

    @PostMapping("/push-tokens")
    fun saveToken(@RequestBody pushToken: PushToken) = service.saveToken(pushToken)
}