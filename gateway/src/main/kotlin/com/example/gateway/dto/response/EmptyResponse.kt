package com.example.gateway.dto.response

import java.time.LocalDateTime

data class EmptyResponse(val message: String, val timestamp: LocalDateTime = LocalDateTime.now())
