package ru.netology.nework.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import ru.netology.nework.filter.AuthApiTokenFilter

@Configuration
@PropertySource("classpath:token.properties")
class ApiAuthTokenConfigurer {

    @Bean
    fun authApiTokenFilter(@Value("\${api.token}") token: String): AuthApiTokenFilter =
        AuthApiTokenFilter(token)
}
