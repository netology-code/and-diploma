package ru.netology.nework.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.netology.nework.dto.PushMessage
import ru.netology.nework.repository.PushTokenRepository
import javax.transaction.Transactional

@Service
@Transactional
class PushService(
    private val pushTokenRepository: PushTokenRepository,
    private val objectMapper: ObjectMapper,
    @Qualifier("PUSH_ENABLED")
    private val pushEnabled: Boolean,
) {

    @Lazy
    @Autowired
    private lateinit var messaging: FirebaseMessaging

    fun send(token: String, message: PushMessage) {
        if (!pushEnabled) {
            return
        }

        messaging.send(
            Message.builder()
                .putData("content", objectMapper.writeValueAsString(message))
                .setToken(token)
                .build()
        )
    }
}