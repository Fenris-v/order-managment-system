package com.example.payment.dto.response

import com.example.payment.enums.UPaymentStatus
import java.time.LocalDateTime

/**
 * Класс представляет сущность ответа с историей платежей пользователя.
 */
data class HistoryResponse(val data: List<TransactionResponse>)

/**
 * Класс представляет сущность платежа.
 */
data class TransactionResponse(
    val amount: Double,
    val status: UPaymentStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
