package com.example.payment.dto.response

import java.time.LocalDateTime

/**
 * Класс представляет сущность ответа на запрос баланса пользователя.
 */
data class UserBalanceResponse(val balance: Double, val updatedAt: LocalDateTime)
