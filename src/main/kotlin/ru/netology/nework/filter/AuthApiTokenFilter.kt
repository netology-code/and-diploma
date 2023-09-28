package ru.netology.nework.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter

class AuthApiTokenFilter(
    private val token: String,
) : OncePerRequestFilter() {

    private companion object {
        const val API_KEY_AUTHORIZATION = "Api-Key"
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        !request.requestURI.contains("/api/")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (request.getHeader(API_KEY_AUTHORIZATION) != token) {
            response.sendError(HttpStatus.FORBIDDEN.value())
            return
        }

        filterChain.doFilter(request, response)
    }
}