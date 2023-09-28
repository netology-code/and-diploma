package ru.netology.nework.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenapiConfiguration {

    @Bean
    fun consumerTypeHeaderOpenAPICustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi: OpenAPI ->
            openApi.components = openApi.components
                .addSecuritySchemes(
                    "Authorization",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("string")
                        .scheme("bearer")
                        .description("Токен авторизации")
                )
                .addSecuritySchemes(
                    "Api-Key",
                    SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .bearerFormat("string")
                        .name("Api-Key")
                        .`in`(SecurityScheme.In.HEADER)
                        .description("Ключ для доступа к серверу. Получите его в ЛК")
                )
        }
}
