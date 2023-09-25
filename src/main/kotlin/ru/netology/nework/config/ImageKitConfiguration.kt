package ru.netology.nework.config

import io.imagekit.sdk.ImageKit
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.util.Lazy

@Configuration
class ImageKitConfiguration {

    @Bean
    fun imageKit(): Lazy<ImageKit> =
        Lazy.of {
            ImageKit.getInstance()
                .apply {
                    config = io.imagekit.sdk.config.Configuration(
                        System.getenv("PublicKey"),
                        System.getenv("PrivateKey"),
                        System.getenv("UrlEndpoint"),
                    ).apply { validate() }
                }
        }
}
