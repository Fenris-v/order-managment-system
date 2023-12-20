package com.example.gateway.dto.response

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ValidatorResponse(
    val messages: MutableMap<String, String>,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int = HttpStatus.UNPROCESSABLE_ENTITY.value()
)
