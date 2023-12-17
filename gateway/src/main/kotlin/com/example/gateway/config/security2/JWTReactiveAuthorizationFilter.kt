package com.example.gateway.config.security2

import com.example.gateway.util.JwtUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JWTReactiveAuthorizationFilter(private val jwtUtil: JwtUtil) : WebFilter {
    companion object {
        private const val BEARER: String = "Bearer "
    }

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return chain.filter(exchange)
        if (!authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange)
        }

        try {
            val token = authHeader.substring(BEARER.length)
            val claims = jwtUtil.extractAllClaims(token)
            val roles = listOf("USER").map { SimpleGrantedAuthority(it) }
            val auth = UsernamePasswordAuthenticationToken(claims.subject, null, roles)

            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        } catch (e: Exception) {
            logger.error("JWT exception", e)
        }

        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.clearContext())
    }
}
