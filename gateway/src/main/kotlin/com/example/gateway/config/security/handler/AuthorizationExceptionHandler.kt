package com.example.gateway.config.security.handler

import com.example.gateway.dto.response.ExceptionDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthorizationExceptionHandler(private val objectMapper: ObjectMapper) {
    fun handleAuthorizationException(message: String, exchange: ServerWebExchange, status: HttpStatus): Mono<Void> {
        val dto = ExceptionDto(message, status.value())
        val response = exchange.response

        response.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        response.setStatusCode(status)
        val dataBuffer: DataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(dto))

        return response.writeWith(Mono.just(dataBuffer))
    }
}
