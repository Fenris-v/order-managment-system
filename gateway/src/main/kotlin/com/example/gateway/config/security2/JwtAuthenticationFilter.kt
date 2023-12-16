package com.example.gateway.config.security2

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtAuthenticationFilter(reactiveAuthenticationManager: ReactiveAuthenticationManager) :
        AuthenticationWebFilter(reactiveAuthenticationManager) {

}
