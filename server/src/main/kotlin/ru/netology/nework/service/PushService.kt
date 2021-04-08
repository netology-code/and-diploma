package ru.netology.nework.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.stereotype.Service
import ru.netology.nework.dto.PushMessage
import ru.netology.nework.repository.PushTokenRepository
import javax.transaction.Transactional

@Service
@Transactional
class PushService(
    private val messaging: FirebaseMessaging,
    private val pushTokenRepository: PushTokenRepository,
    private val objectMapper: ObjectMapper,
) {
    fun send(token: String, message: PushMessage) {
        messaging.send(
            Message.builder()
                .putData("content", objectMapper.writeValueAsString(message))
                .setToken(token)
                .build()
        )
    }
}