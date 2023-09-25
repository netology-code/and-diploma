package ru.netology.nework.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import ru.netology.nework.dto.AnonymousUser
import ru.netology.nework.filter.AuthTokenFilter
import ru.netology.nework.service.UserService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class AppWebSecurityConfigurerAdapter {
    @Autowired
    @Lazy
    lateinit var userService: UserService

    @Bean
    fun passwordEncoder(): PasswordEncoder = SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8()

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers("/images/*", "/avatars/*", "/media/*")
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf().disable()
            .exceptionHandling()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterAfter(AuthTokenFilter(userService), BasicAuthenticationFilter::class.java)
            .anonymous {
                it.principal(AnonymousUser).authorities(*AnonymousUser.authorities.toTypedArray())
            }
            .authorizeHttpRequests()
            .anyRequest()
            .permitAll()
            .let {
                http.build()
            }
}