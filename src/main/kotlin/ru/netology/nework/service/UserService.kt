package ru.netology.nework.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.User
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.entity.TokenEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PasswordNotMatchException
import ru.netology.nework.exception.UserRegisteredException
import ru.netology.nework.repository.TokenRepository
import ru.netology.nework.repository.UserRepository
import java.security.SecureRandom
import java.util.Base64

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mediaService: MediaService,
) : UserDetailsService {
    fun getAll(): List<UserResponse> = userRepository.findAll()
        .map(UserEntity::toResponse)

    fun register(login: String, pass: String, name: String, file: MultipartFile?): Token {
        if (userRepository.findByLoginIgnoreCase(login) != null) {
            throw UserRegisteredException()
        }

        val avatar = file?.let {
            mediaService.save(it)
        }

        return userRepository.save(
            UserEntity(
                0L,
                login,
                passwordEncoder.encode(pass),
                name,
                avatar?.url,
            )
        ).let { user ->
            val token = Token(user.id, generateToken())
            tokenRepository.save(TokenEntity(token.token, user))
            token
        }
    }

    fun login(login: String, pass: String): Token = userRepository
        .findByLoginIgnoreCase(login)
        ?.let { user ->
            if (!passwordEncoder.matches(pass, user.password)) {
                throw PasswordNotMatchException()
            }
            val token = Token(user.id, generateToken())
            tokenRepository.save(TokenEntity(token.token, user))
            token
        } ?: throw NotFoundException()

    fun getByToken(token: String): User? = tokenRepository
        .findByIdOrNull(token)
        ?.user
        ?.toDto()

    override fun loadUserByUsername(username: String?): UserDetails =
        userRepository.findByLoginIgnoreCase(username) ?: throw UsernameNotFoundException(username)

    private fun generateToken(): String = ByteArray(128).apply {
        SecureRandom().nextBytes(this)
    }.let {
        Base64.getEncoder().withoutPadding().encodeToString(it)
    }

    fun getById(id: Long): UserResponse =
        userRepository.findById(id)
            .takeIf { it.isPresent }
            ?.get()
            ?.toResponse()
            ?: throw NotFoundException()
}
