package ru.netology.nework.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DatabaseConfiguration {

    @Bean
    @Primary
    fun dataSource(
        @Value("\${SPRING_DATASOURCE_USERNAME:sa}") username: String,
        @Value("\${SPRING_DATASOURCE_PASSWORD:password}") password: String,
        @Value("\${SPRING_DATASOURCE_URL:jdbc:h2:mem:testdb}") url: String,
    ): DataSource =
        DataSourceBuilder
            .create()
            .username(username)
            .password(password)
            .url(url)
            .build()
}
