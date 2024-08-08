package com.example.payment.dto.umoney.request

import com.example.payment.dto.umoney.Amount
import com.example.payment.enums.ConfirmationTypeEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/**
 * Класс представляет сущность запроса к внешнему API на проведение платежа.
 */
data class UMoneyPaymentRequest(
    val amount: Amount,
    val receipt: Receipt,
    @JsonProperty("payment_method_data") val paymentMethodData: PaymentMethodData,
    val confirmation: Confirmation,
    val description: String,
    @get:JsonProperty("save_payment_method") val savePaymentMethod: Boolean = false, // параметр отвечающий за сохранение платежных данных
    @get:JsonProperty("payment_method_id") val paymentMethodId: UUID? = null,
    val capture: Boolean = true, // параметр отвечающий за моментальное списание, без подтверждения
    val metadata: Map<String, Any>? = null
)

/**
 * Класс представляет сущность метода оплаты.
 */
data class PaymentMethodData(val type: String)

/**
 * Класс представляет сущность подтверждения.
 */
data class Confirmation(
    @get:JsonProperty("return_url") val returnUrl: String,
    val type: String = ConfirmationTypeEnum.REDIRECT.value
)

/**
 * Класс представляет сущность чека.
 */
data class Receipt(val customer: Customer, val items: List<UMoneyProduct>)

/**
 * Класс представляет сущность покупателя.
 */
data class Customer(
    @get:JsonProperty("full_name") val fullName: String,
    val email: String,
    val inn: String? = null,
    val phone: String? = null
)

/**
 * Класс представляет сущность продукта.
 */
data class UMoneyProduct(
    val description: String,
    val amount: Amount,
    @get:JsonProperty("vat_code") val vatCode: Int,
    val quantity: Int
)
