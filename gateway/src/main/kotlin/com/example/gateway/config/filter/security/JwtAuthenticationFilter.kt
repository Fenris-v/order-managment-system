package com.example.gateway.config.filter.security

import com.example.gateway.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtUtil: JwtUtil, private val userDetailsService: UserDetailsService) :
        OncePerRequestFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || authHeader.startsWith(BEARER_PREFIX)) {
            chain.doFilter(request, response)
            return
        }

        val jwt: String = authHeader.substring(BEARER_PREFIX.length)
        val username: String = jwtUtil.extractUsername(jwt)
        if (SecurityContextHolder.getContext().authentication == null) {
            val user: UserDetails = userDetailsService.loadUserByUsername(username)
            if (jwtUtil.isTokenValid(jwt, user)) {
                val authToken = UsernamePasswordAuthenticationToken(user, null, user.authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        chain.doFilter(request, response)
    }
}
