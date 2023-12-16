package com.example.gateway.config.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtSecurityContextRepository(private val jwtAuthenticationManager: JwtAuthenticationManager) :
        ServerSecurityContextRepository {
    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        return Mono.empty()
    }

    override fun load(exchange: ServerWebExchange?): Mono<SecurityContext> {
        val authHeader: String? = exchange
            ?.request
            ?.headers
            ?.getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val jwt: String = authHeader.substring(7)
            val auth = UsernamePasswordAuthenticationToken(jwt, jwt)

            return jwtAuthenticationManager
                .authenticate(auth)
                .onErrorResume {
//                    exchange.response.setStatusCode(HttpStatus.UNAUTHORIZED)
//                    val errorMessage = it?.message ?: "Unauthorized"
                    throw AuthenticationException2(it?.message ?: "Unauthorized", it)
//                    val dataBuffer = exchange.response.bufferFactory().wrap(errorMessage.toByteArray())
//                    exchange.response.writeWith(Mono.just(dataBuffer))
                }
                .map {
                    SecurityContextImpl(it)
                }
        }

        return Mono.empty()
    }
}
