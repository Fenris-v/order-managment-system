package com.example.payment.util.deserializer

import com.example.payment.enums.UPaymentStatus
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * Класс-десериализатор статуса платежа. Обрабатывает статусы из ответа API UMoney и преобразует их
 * во внутренние значения.
 */
class UPaymentStatusDeserializer : JsonDeserializer<UPaymentStatus>() {

    /**
     * Метод десериализации. Обрабатывает статусы из ответа API UMoney и преобразует их
     * во внутренние значения.
     *
     * @param parser - парсер JSON
     * @param context - контекст десериализации
     */
    override fun deserialize(parser: JsonParser, context: DeserializationContext): UPaymentStatus {
        val value = parser.valueAsString
        return UPaymentStatus.values().firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Неизвестный статус: $value")
    }
}
