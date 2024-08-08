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
    @JsonProperty("id") var id: UUID,
    @JsonProperty("status") @JsonDeserialize(using = UPaymentStatusDeserializer::class) val status: UPaymentStatus,
    @JsonProperty("amount") val amount: Amount,
    @JsonProperty("description") val description: String,
    @JsonProperty("recipient") val recipient: Recipient,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("confirmation") val confirmation: Confirmation?,
    @JsonProperty("paid") val paid: Boolean,
    @JsonProperty("refundable") val refundable: Boolean,
    @JsonProperty("metadata") val metadata: Map<String, String>
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
    @JsonProperty("type") val type: String,
    @JsonProperty("return_url") val returnUrl: String? = null,
    @JsonProperty("confirmation_url") val confirmationUrl: String? = null
)
