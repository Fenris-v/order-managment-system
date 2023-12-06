package com.example.gateway.config.security

import com.example.gateway.util.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomAuthenticationManager : ReactiveAuthenticationManager {
    @Autowired
    private lateinit var jwtUtil: JwtUtil
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val jwt: String = authentication?.credentials.toString()
        val userName: String = jwtUtil.extractUsername(jwt)
//        if (userName != null) {
//            jwtUtil.isTokenValid(authToken)
//        }

//        val claims: Claims = jwtUtil.extractAllClaims(authToken)
//        val roles: List<String> = jwtUtil.extractClaim("role", Claims::getExpiration)
        val authenticationToken = UsernamePasswordAuthenticationToken(userName, null)

        return Mono.just(authenticationToken)
    }
}
