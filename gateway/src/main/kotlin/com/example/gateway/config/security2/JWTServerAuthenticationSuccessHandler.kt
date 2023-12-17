package com.example.gateway.config.security2

import com.example.gateway.util.JwtUtil
import com.example.gateway.util.RefreshTokenUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class JWTServerAuthenticationSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val refreshTokenUtil: RefreshTokenUtil
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?
    ): Mono<Void> {
        val principal = authentication?.principal
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")

        when (principal) {
            is User -> {
                val accessToken: String = jwtUtil.generateToken(principal)
                val refreshToken: String = refreshTokenUtil.generateToken(principal)
                webFilterExchange?.exchange?.response?.headers?.set(HttpHeaders.AUTHORIZATION, accessToken)
                webFilterExchange?.exchange?.response?.headers?.set("JWT-Refresh-Token", refreshToken)
            }
        }

        return Mono.empty()
    }
}
