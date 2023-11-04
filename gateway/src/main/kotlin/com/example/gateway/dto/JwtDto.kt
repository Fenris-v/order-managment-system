package com.example.gateway.dto

import java.time.LocalDateTime

class JwtDto(private val jwt: String, private val timestamp: LocalDateTime = LocalDateTime.now())
