package com.example.gateway.config.security.handler

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JWTServerAuthenticationFailureHandler(private val exceptionHandler: AuthorizationExceptionHandler) :
        ServerAuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange?,
        exception: AuthenticationException?
    ): Mono<Void> {
        val exchange: ServerWebExchange = webFilterExchange?.exchange
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")

        return exceptionHandler
            .handleAuthorizationException(exception?.message ?: "Unauthorized", exchange, HttpStatus.UNAUTHORIZED)
    }
}
