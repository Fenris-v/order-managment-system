package com.example.payment.enums

/**
 * Перечисление возможных статусов платежа.
 */
enum class UPaymentStatus(val value: String) {
    /**
     * Платеж ожидает подтверждения
     */
    PENDING("pending"),

    /**
     * Платеж ожидает подтверждения
     */
    WAITING_FOR_CAPTURE("waiting_for_capture"),

    /**
     * Платеж отменен
     */
    CANCELED("canceled"),

    /**
     * Платеж подтвержден
     */
    SUCCEEDED("succeeded")
}
