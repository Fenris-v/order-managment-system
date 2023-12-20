package com.example.gateway.config.security.handler

import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.model.User
import com.example.gateway.util.JwtUtil
import com.example.gateway.util.RefreshTokenUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class JWTServerAuthenticationSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val refreshTokenUtil: RefreshTokenUtil
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?
    ): Mono<Void> {
        if (authentication == null || webFilterExchange == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")
        }

        when (val principal = authentication.principal) {
            is User -> {
                val accessToken: String = jwtUtil.generateToken(principal)
                val refreshToken: String = refreshTokenUtil.generateToken(principal)

                val responseDto = JwtResponse(accessToken, refreshToken)
                val response = webFilterExchange.exchange.response
                response.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                val dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(responseDto))

                return response.writeWith(Mono.just(dataBuffer))
            }
        }

        return Mono.empty()
    }
}
