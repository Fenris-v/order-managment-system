//package com.example.gateway.config
//
//import org.springframework.http.HttpStatus
//import org.springframework.security.core.AuthenticationException
//import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
//import org.springframework.stereotype.Component
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//
//@Component
//class CustomAuthenticationFailureHandler : ServerAuthenticationFailureHandler {
//    override fun onAuthenticationFailure(
//        exchange: ServerWebExchange,
//        exception: AuthenticationException?
//    ): Mono<Void> {
//        return Mono.fromRunnable {
//            return exchange.response.statusCode = HttpStatus.UNAUTHORIZED
//        }
//    }
//}
