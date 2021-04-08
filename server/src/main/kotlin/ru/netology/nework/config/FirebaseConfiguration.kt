package ru.netology.nework.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.netology.nework.dbUrl
import java.io.FileInputStream

@Configuration
class FirebaseConfiguration {
    @Bean
    fun firebaseApp(): FirebaseApp =
        FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(FileInputStream("fcm.json")))
                .setDatabaseUrl(dbUrl)
                .build()
        )

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging =
        FirebaseMessaging.getInstance(firebaseApp)
}