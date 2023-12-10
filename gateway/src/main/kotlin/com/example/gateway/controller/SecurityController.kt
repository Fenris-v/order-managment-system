package com.example.gateway.controller

import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/user")
class SecurityController(private val userService: UserService) {
    @PostMapping("/login")
    fun login(exchange: ServerWebExchange, @RequestBody authDto: AuthDto): Mono<JwtResponse> {
        return userService.authenticateUser(authDto).map { it }
    }

    @PostMapping("/register")
    fun register(@RequestBody @Valid authDto: AuthDto): Mono<ResponseEntity<UserDto>> {
        return userService.createUser(authDto).map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestParam refreshToken: String): Mono<JwtResponse> {
        return Mono.empty()
    }

    @GetMapping("/test")
    fun test(): Mono<String> {
        return Mono.just("test")
    }
}
