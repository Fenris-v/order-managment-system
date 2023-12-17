package com.example.gateway.config.security2

import com.example.gateway.dto.request.security.AuthDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtConverter(private val objectMapper: ObjectMapper) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {
        if (exchange == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")
        }

        try {
            return exchange.request.body.flatMap {
                val authDto = objectMapper.readValue(it.toString(), AuthDto::class.java)
                UsernamePasswordAuthenticationToken(authDto.username, authDto.password)
            }
        } catch (e: Exception) {
            return Mono.empty()
        }
    }
}
