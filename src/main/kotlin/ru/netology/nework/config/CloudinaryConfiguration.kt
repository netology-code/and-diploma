package ru.netology.nework.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.cloudinary.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy

@Configuration
class CloudinaryConfiguration {

    @Bean
    @Lazy
    fun cloudinary(
        @Value("\${CLOUD_NAME:#{null}}") cloudName: String?,
        @Value("\${API_KEY:#{null}}") apiKey: String?,
        @Value("\${API_SECRET:#{null}}") apiSecret: String?,
    ): Cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret
        )
    )
}
