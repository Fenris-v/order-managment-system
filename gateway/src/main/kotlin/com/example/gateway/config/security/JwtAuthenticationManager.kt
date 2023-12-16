package com.example.gateway.config.security

import com.example.gateway.util.JwtUtil
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(private val jwtUtil: JwtUtil) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val jwt: String = authentication?.credentials.toString()
        val role: List<String> = listOf("USER")
        val authorities: List<SimpleGrantedAuthority> = role.stream().map { SimpleGrantedAuthority(it) }.toList()

        return jwtUtil
            .extractUsername(jwt)
            .flatMap {
                Mono.just<Authentication>(UsernamePasswordAuthenticationToken(it, null, authorities))
            }
//            .onErrorResume {
//                Mono.empty()
//            }
    }
}
//        if (userName != null) {
//            jwtUtil.isTokenValid(authToken)
//        }

//        val claims: Claims = jwtUtil.extractAllClaims(authToken)
//        val roles: List<String> = jwtUtil.extractClaim("role", Claims::getExpiration)
//        val authenticationToken = UsernamePasswordAuthenticationToken(userName, null)
//
//        return Mono.just(authenticationToken)

