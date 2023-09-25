package ru.netology.nework.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.netology.nework.mapper.EventEntityToDtoMapper
import ru.netology.nework.mapper.PostEntityToDtoMapper
import ru.netology.nework.repository.UserRepository

@Configuration
class MappersConfiguration {

    @Bean
    fun eventEntityMapper(userRepository: UserRepository) = EventEntityToDtoMapper(userRepository)

    @Bean
    fun postEntityMapper(userRepository: UserRepository) = PostEntityToDtoMapper(userRepository)
}
