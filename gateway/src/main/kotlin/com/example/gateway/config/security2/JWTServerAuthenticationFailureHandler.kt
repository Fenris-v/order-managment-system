package com.example.gateway.config.security2

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class JWTServerAuthenticationFailureHandler(private val objectMapper: ObjectMapper) :
        ServerAuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange?,
        exception: AuthenticationException?
    ): Mono<Void> {
        val exchange = webFilterExchange?.exchange
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")

        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorMessage = "Unauthorized: " + exception?.message
        val dataBuffer = response.bufferFactory().wrap(errorMessage.toByteArray())

        return exchange.response.writeWith(Mono.just(dataBuffer))
    }
}
