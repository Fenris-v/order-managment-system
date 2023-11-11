package com.example.gateway.controller

import com.example.gateway.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class SecurityController(private val jwtUtil: JwtUtil) {
//    @PostMapping("/api/v1/user/login")
//    fun login(): Mono<String> {
//        val user = User(1L, "admin@mail.com", "password")
//        return Mono.just(jwtUtil.generateJWT(user))
//    }

    @PostMapping("/signup")
    fun singUp(): ResponseEntity<ResponseStatus> {
//        log.debug("Receive userCreateDto:$userCreateDto")
//        userService.createUser(userCreateDto)
//        log.debug("User created successfully")
        return ResponseEntity.ok().build()
    }

    @PostMapping("/signin")
    fun login(): ResponseEntity<ResponseStatus> {
        return ResponseEntity.ok().build()
//        return authService.login(loginRequest)
    }
}
