package com.example.gateway.config.filter

import com.example.gateway.config.security.SecurityConfig.Companion.BEARER
import com.example.gateway.config.security.handler.AuthorizationExceptionHandler
import com.example.gateway.model.User
import com.example.gateway.repository.AccessTokenRepository
import com.example.gateway.service.UserDetailsService
import com.example.gateway.util.jwt.JwtUtil
import com.example.starter.utils.dto.response.ExceptionDto
import com.example.starter.utils.exception.EntityNotFoundException
import com.example.starter.utils.exception.ForbiddenException
import com.example.starter.utils.exception.JsonParseException
import com.example.starter.utils.exception.UnprocessableException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.stream.Stream

private val log: KLogger = KotlinLogging.logger {}

/**
 * Фильтр для обработки авторизации на основе JWT.
 * <p>
 * Этот фильтр извлекает токен JWT из заголовка Authorization, валидирует его и выполняет проверки авторизации. Если
 * токен действителен, пользователь аутентифицируется, и запрос получает разрешение на продолжение. Если токен не
 * действителен или отсутствует, отправляется ответ с отказом в доступе.
 */
@Component
class JWTReactiveAuthorizationFilter(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val userDetailsService: UserDetailsService,
    private val accessTokenRepository: AccessTokenRepository,
    private val exceptionHandler: AuthorizationExceptionHandler
) : WebFilter, NonGlobalFilter {
    companion object {
        const val DEFAULT_EXCEPTION_MESSAGE: String = "Произошла неизвестная ошибка при попытке авторизации"
    }

    /**
     * Получает токен из заголовков запроса, проверяет наличие токена в БД, его валидность и авторизует пользователя
     *
     * @param exchange объект ServerWebExchange, представляющий текущий обмен
     * @param chain    цепочка фильтров для продолжения обработки запроса
     * @return Mono, представляющий завершение обработки запроса
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader: String = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: return chain.filter(exchange)
        if (!authHeader.startsWith(BEARER)) {
            return chain.filter(exchange)
        }

        log.debug { "Обработка токена" }
        val token: String = authHeader.substring(BEARER.length)
        return accessTokenRepository
            .existsById(jwtUtil.extractTokenId(token))
            .flatMap { exists ->
                if (exists && jwtUtil.isValidToken(token)) handleValidToken(token, exchange, chain)
                else handleNotValidToken(exchange)
            }
            .onErrorResume { ex ->
                exceptionHandler.handleAuthorizationException(
                    ex.message ?: DEFAULT_EXCEPTION_MESSAGE,
                    exchange,
                    HttpStatus.FORBIDDEN
                )
            }
    }

    private fun handleNotValidToken(exchange: ServerWebExchange): Mono<Void> {
        log.debug { "Обработка невалидного токена" }
        try {
            val response = exchange.response
            response.setStatusCode(HttpStatus.UNAUTHORIZED)
            response.headers.contentType = MediaType.APPLICATION_JSON

            val dto = ExceptionDto("Unauthorized", HttpStatus.UNAUTHORIZED.value())
            val dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(dto))

            return response.writeWith(Mono.just(dataBuffer))
        } catch (ex: java.lang.Exception) {
            log.error(ex) { "JWT exception" }
            throw JsonParseException()
        }
    }

    private fun handleValidToken(token: String, exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        log.debug { "Обработка валидного токена" }
        return userDetailsService
            .findByUsername(jwtUtil.extractUsername(token))
            .switchIfEmpty(Mono.error { EntityNotFoundException() })
            .flatMap { userDetails ->
                if (userDetails !is User) {
                    return@flatMap Mono.error(ForbiddenException())
                }

                val roles: Collection<SimpleGrantedAuthority> = Stream.of("USER")
                    .map { role: String -> SimpleGrantedAuthority(role) }
                    .toList()
                val auth =
                    UsernamePasswordAuthenticationToken(jwtUtil.extractUsername(token), null, roles)
                chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
            }.onErrorResume { ex ->
                log.error(ex) { ex.message }
                val status = when (ex) {
                    is ResponseStatusException -> ex.statusCode
                    is UnprocessableException -> HttpStatus.UNPROCESSABLE_ENTITY
                    else -> HttpStatus.FORBIDDEN
                }

                exceptionHandler.handleAuthorizationException(ex.message ?: DEFAULT_EXCEPTION_MESSAGE, exchange, status)
            }
    }
}
