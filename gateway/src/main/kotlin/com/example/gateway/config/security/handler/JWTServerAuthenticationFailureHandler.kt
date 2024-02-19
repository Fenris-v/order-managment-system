package com.example.gateway.config.security.handler

import com.example.gateway.exception.BadRequestException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.lang.NonNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

/**
 * Обработчик события сбоя аутентификации на сервере для JWT-аутентификации.
 * <p>
 * Этот обработчик вызывается в случае сбоя аутентификации пользователя (валидации JWT) и предоставляет логику обработки
 * ошибки аутентификации.
 */
@Component
class JWTServerAuthenticationFailureHandler(private val exceptionHandler: AuthorizationExceptionHandler) :
    ServerAuthenticationFailureHandler {
    /**
     * Обрабатывает событие сбоя аутентификации на сервере.
     *
     * @param webFilterExchange объект WebFilterExchange, представляющий текущий обмен
     * @param exception         исключение аутентификации, вызвавшее сбой
     * @return Mono, представляющий завершение обработки события сбоя аутентификации
     */
    override fun onAuthenticationFailure(
        @NonNull webFilterExchange: WebFilterExchange,
        @NonNull exception: AuthenticationException
    ): Mono<Void> {
        log.info { "Аутентификации неуспешна: ${exception.message}" }
        val exchange = webFilterExchange.exchange ?: throw BadRequestException()

        val status =
            if (exception is BadCredentialsException) HttpStatus.UNPROCESSABLE_ENTITY
            else HttpStatus.UNAUTHORIZED

        return exceptionHandler.handleAuthorizationException(exception.message!!, exchange, status)
    }
}
