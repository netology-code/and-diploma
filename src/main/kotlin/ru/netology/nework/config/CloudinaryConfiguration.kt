package ru.netology.nework.config

import com.cloudinary.Cloudinary
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class CloudinaryConfiguration {

    @Bean
    @Lazy
    fun cloudinary(): Cloudinary = Cloudinary()
}
