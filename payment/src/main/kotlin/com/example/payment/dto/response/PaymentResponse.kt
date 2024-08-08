package com.example.payment.dto.response

import java.util.UUID

/**
 * Класс представляет сущность ответа на запрос ссылки на оплату.
 */
data class PaymentResponse(val paymentLink: String, val paymentId: UUID)
