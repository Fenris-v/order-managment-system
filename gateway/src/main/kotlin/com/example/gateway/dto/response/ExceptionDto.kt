package com.example.gateway.dto.response

import java.time.LocalDateTime


data class ExceptionDto(val message: String, val status: Int, val timestamp: LocalDateTime = LocalDateTime.now())
