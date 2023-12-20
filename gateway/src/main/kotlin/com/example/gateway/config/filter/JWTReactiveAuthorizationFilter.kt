package com.example.gateway.config.filter

import com.example.gateway.config.security.SecurityConfig.Companion.BEARER
import com.example.gateway.config.security.handler.AuthorizationExceptionHandler
import com.example.gateway.dto.response.ExceptionDto
import com.example.gateway.repository.BlacklistTokenRepository
import com.example.gateway.util.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JWTReactiveAuthorizationFilter(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val exceptionHandler: AuthorizationExceptionHandler,
    private val blacklistTokenRepository: BlacklistTokenRepository
) : WebFilter {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader: String = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: return chain.filter(exchange)
        if (!authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange)
        }

        val token: String = authHeader.substring(BEARER.length)
        return blacklistTokenRepository
            .existsByToken(token)
            .flatMap { if (it) handleBlacklistedToken(exchange) else handleValidToken(token, exchange, chain) }
    }

    private fun handleBlacklistedToken(exchange: ServerWebExchange): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.statusCode = HttpStatus.FORBIDDEN
        response.headers.contentType = MediaType.APPLICATION_JSON

        val dto = ExceptionDto("Forbidden", HttpStatus.FORBIDDEN.value())
        val dataBuffer: DataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(dto))

        return response.writeWith(Mono.just(dataBuffer))
    }

    private fun handleValidToken(token: String, exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        try {
            val claims = jwtUtil.extractAllClaims(token)
            val roles = listOf("USER").map { SimpleGrantedAuthority(it) }
            val auth = UsernamePasswordAuthenticationToken(claims.subject, null, roles)

            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        } catch (ex: Exception) {
            logger.error("JWT exception", ex)
            return exceptionHandler
                .handleAuthorizationException(ex.message ?: "JWT exception", exchange, HttpStatus.FORBIDDEN)
        }
    }
}
