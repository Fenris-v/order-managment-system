package com.example.payment.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

/**
 * Запись, представляющая объект данных для запроса ссылки на оплату.
 */
data class PaymentRequest(@JsonProperty("amount") @field:Min(1) @NotBlank val amount: Double)
