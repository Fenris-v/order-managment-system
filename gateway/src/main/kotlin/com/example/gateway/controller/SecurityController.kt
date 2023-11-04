package com.example.gateway.controller

import com.example.gateway.model.User
import com.example.gateway.util.JwtUtil
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class SecurityController(private val jwtUtil: JwtUtil) {
    @PostMapping("/api/v1/user/login")
    fun login(): Mono<String> {
        val user = User(1L, "admin@mail.com", "password")
        return Mono.just(jwtUtil.generateJWT(user))
    }
}
