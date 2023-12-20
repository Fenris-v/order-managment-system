package com.example.gateway.config.security

import com.example.gateway.dto.request.security.AuthDto
import org.springframework.core.ResolvableType
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.HttpMessageReader
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.Collections

@Component
class JwtConverter(private val serverCodecConfigurer: ServerCodecConfigurer) : ServerAuthenticationConverter {
    private val authDtoType = ResolvableType.forClass(AuthDto::class.java)

    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {
        if (exchange == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")
        }

        val request = exchange.request
        return serverCodecConfigurer.readers
            .stream()
            .filter { reader: HttpMessageReader<*> -> reader.canRead(this.authDtoType, MediaType.APPLICATION_JSON) }
            .findFirst()
            .orElseThrow { IllegalStateException("No JSON reader for UsernamePasswordContent") }
            .readMono(authDtoType, request, Collections.emptyMap())
            .cast(AuthDto::class.java)
            .map { UsernamePasswordAuthenticationToken(it.username, it.password) }
    }
}
