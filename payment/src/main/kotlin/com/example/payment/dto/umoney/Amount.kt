package com.example.payment.dto.umoney

import com.example.payment.enums.CurrencyEnum
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Класс представляет сущность для отображения суммы заказа в UMoney.
 */
data class Amount(
    @JsonProperty("value") val value: Double,
    @JsonProperty("currency") val currency: CurrencyEnum = CurrencyEnum.RUB
)
