package ru.netology.nework.filter

import jakarta.servlet.FilterChain
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import ru.netology.nework.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class AuthTokenFilter(
    private val userService: UserService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let { token ->
            val user = userService.getByToken(token) ?: run {
                response.sendError(HttpStatus.FORBIDDEN.value())
                return
            }
            val authToken = UsernamePasswordAuthenticationToken(user, null, user.authorities.map(::SimpleGrantedAuthority))
            SecurityContextHolder.getContext().authentication = authToken
        }

        filterChain.doFilter(request, response)
    }
}