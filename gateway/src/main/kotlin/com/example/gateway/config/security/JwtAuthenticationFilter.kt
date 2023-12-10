package com.example.gateway.config.security

import com.example.gateway.dto.response.ExceptionDto
import com.example.gateway.util.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(private val objectMapper: ObjectMapper, private val jwtUtil: JwtUtil) : WebFilter {
    companion object {
        private const val BEARER: String = "Bearer "
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authorizationHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
//            return chain.filter(exchange)
            return createUnauthorizedResponse(exchange)
        }

        val token = authorizationHeader.substring(BEARER.length)
        return jwtUtil.isValidToken(token)
            .flatMap {
                if (!it) {
                    createUnauthorizedResponse(exchange)
                } else {
                    chain.filter(exchange)
                }
            }.onErrorResume {
                createUnauthorizedResponse(exchange, it.message ?: "Unauthorized")
            }
    }

    private fun createUnauthorizedResponse(exchange: ServerWebExchange): Mono<Void> {
        return createUnauthorizedResponse(exchange, "Unauthorized")
    }

    private fun createUnauthorizedResponse(exchange: ServerWebExchange, message: String): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val exceptionDto = ExceptionDto(message, HttpStatus.UNAUTHORIZED.value())
        val responseBody: String = objectMapper.writeValueAsString(exceptionDto)
        val dataBuffer = response.bufferFactory().wrap(responseBody.toByteArray())

        return response.writeWith(Mono.just(dataBuffer))
    }
}
