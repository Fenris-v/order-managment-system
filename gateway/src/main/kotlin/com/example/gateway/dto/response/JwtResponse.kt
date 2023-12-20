package com.example.gateway.dto.response

import java.time.LocalDateTime

data class JwtResponse(
    val authToken: String,
    val refreshToken: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
