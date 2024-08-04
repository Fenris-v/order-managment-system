package com.example.payment.dto.umoney

import com.example.payment.enums.CurrencyEnum

/**
 * Класс представляет сущность для отображения суммы заказа в UMoney.
 */
data class Amount(val value: Double, val currency: CurrencyEnum = CurrencyEnum.RUB)
