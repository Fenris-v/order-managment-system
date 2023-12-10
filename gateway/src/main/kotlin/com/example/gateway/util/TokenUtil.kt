package com.example.gateway.util

import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

interface TokenUtil {
    fun generateToken(userDetails: UserDetails): Mono<String>
}
