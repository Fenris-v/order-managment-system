package com.example.gateway.controller

import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.request.security.RefreshDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.exception.ForbiddenException
import com.example.gateway.service.UserDetailsService
import com.example.gateway.util.JwtUtil
import io.jsonwebtoken.JwtException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/user")
class SecurityController(private val userService: UserDetailsService, private val jwtUtil: JwtUtil) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid authDto: AuthDto, authentication: Authentication?): Mono<ResponseEntity<UserDto>> {
        return userService.createUser(authDto).map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody refreshDto: RefreshDto): Mono<JwtResponse> {
        try {
            if (!jwtUtil.isValidToken(refreshDto.refreshToken)) {
                throw ForbiddenException()
            }

            return userService.refresh(refreshDto.refreshToken)
        } catch (ex: JwtException) {
            throw ForbiddenException()
        }
    }

    @PostMapping("/logout")
    fun logout(exchange: ServerWebExchange): Mono<Void> {
        return userService.logout(exchange)
    }
}
