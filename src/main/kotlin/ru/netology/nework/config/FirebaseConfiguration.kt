package ru.netology.nework.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
class FirebaseConfiguration {
    @Bean("PUSH_ENABLED")
    fun pushEnabled(): Boolean = Files.exists(Paths.get("fcm.json"))

    @Lazy
    @Bean
    fun firebaseApp(): FirebaseApp =
        FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(FileInputStream("fcm.json")))
                .build()
        )

    @Lazy
    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging =
        FirebaseMessaging.getInstance(firebaseApp)
}