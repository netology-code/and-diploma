package ru.netology.nework.service

import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.PushToken
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.User
import ru.netology.nework.entity.PushTokenEntity
import ru.netology.nework.entity.TokenEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.exception.NotFoundException
import ru.netology.nework.exception.PasswordNotMatchException
import ru.netology.nework.extensions.principalOrNull
import ru.netology.nework.repository.PushTokenRepository
import ru.netology.nework.repository.TokenRepository
import ru.netology.nework.repository.UserRepository
import java.security.SecureRandom
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val pushTokenRepository: PushTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mediaService: MediaService,
) : UserDetailsService {
    fun getAll(): List<User> = userRepository.findAll()
        .map(UserEntity::toDto)

    fun create(login: String, pass: String, name: String, avatar: String): User = userRepository.save(
        UserEntity(
            0L,
            login,
            passwordEncoder.encode(pass),
            name,
            avatar,
        )
    ).toDto()

    fun register(login: String, pass: String, name: String, file: MultipartFile?): Token {
        val avatar = file?.let {
            mediaService.saveAvatar(it)
        }

        return userRepository.save(
            UserEntity(
                0L,
                login,
                passwordEncoder.encode(pass),
                name,
                avatar?.id ?: "noavatar.png",
            )
        ).let { user ->
            val token = Token(user.id, generateToken())
            tokenRepository.save(TokenEntity(token.token, user))
            token
        }
    }

    fun login(login: String, pass: String): Token = userRepository
        .findByLogin(login)
        ?.let { user ->
            if (!passwordEncoder.matches(pass, user.password)) {
                throw PasswordNotMatchException()
            }
            val token = Token(user.id, generateToken())
            tokenRepository.save(TokenEntity(token.token, user))
            token
        } ?: throw NotFoundException()

    fun getByLogin(login: String): User = userRepository
        .findByLogin(login)
        ?.toDto() ?: throw NotFoundException()

    fun getByToken(token: String): User = tokenRepository
        .findByIdOrNull(token)
        ?.user
        ?.toDto() ?: throw NotFoundException()

    override fun loadUserByUsername(username: String?): UserDetails =
        userRepository.findByLogin(username) ?: throw UsernameNotFoundException(username)

    fun saveInitialToken(userId: Long, value: String): Token =
        userRepository.findByIdOrNull(userId)
            ?.let { user ->
                val token = Token(userId, value)
                tokenRepository.save(TokenEntity(token.token, user))
                token
            } ?: throw NotFoundException()

    private fun generateToken(): String = ByteArray(128).apply {
        SecureRandom().nextBytes(this)
    }.let {
        Base64.getEncoder().withoutPadding().encodeToString(it)
    }

    fun saveToken(pushToken: PushToken) {
        val userId = principalOrNull()?.id ?: 0
        pushTokenRepository.findByToken(pushToken.token)
            .orElse(PushTokenEntity(0, pushToken.token, userId))
            .let {
                if (it.id == 0L) pushTokenRepository.save(it) else it.userId = userId
            }
    }
}