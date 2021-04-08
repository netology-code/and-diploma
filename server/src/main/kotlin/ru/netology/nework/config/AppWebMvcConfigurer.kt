package ru.netology.nework.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class AppWebMvcConfigurer(@Value("\${app.media-location}") private val mediaLocation: String) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {
            override fun preHandle(
                request: HttpServletRequest,
                response: HttpServletResponse,
                handler: Any,
            ): Boolean {
                if (
                    request.requestURI.startsWith("/api/slow") ||
                    request.requestURI.startsWith("/avatars") ||
                    request.requestURI.startsWith("/media")
                ) {
                    Thread.sleep(5_000)
                }
                return true
            }
        })
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/**")
            .addResourceLocations(mediaLocation)
    }
}