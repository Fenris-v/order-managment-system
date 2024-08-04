package com.example.payment.dto.umoney.response

import com.example.payment.dto.umoney.Amount
import com.example.payment.enums.UPaymentStatus
import com.example.payment.util.deserializer.UPaymentStatusDeserializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalDateTime
import java.util.UUID

/**
 * Класс представляет сущность ответа от API UMoney.
 */
data class UMoneyResponse(
    var id: UUID,
    @JsonDeserialize(using = UPaymentStatusDeserializer::class) val status: UPaymentStatus,
    val amount: Amount,
    val description: String,
    val recipient: Recipient,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    val confirmation: Confirmation?,
    val paid: Boolean,
    val refundable: Boolean,
    val metadata: Map<String, String>
)

/**
 * Класс представляет сущность получателя платежа.
 */
data class Recipient(
    @JsonProperty("account_id") val accountId: String,
    @JsonProperty("gateway_id") val gatewayId: String
)

/**
 * Класс представляет сущность подтверждения платежа.
 */
data class Confirmation(
    val type: String,
    @JsonProperty("return_url") val returnUrl: String? = null,
    @JsonProperty("confirmation_url") val confirmationUrl: String? = null
)
