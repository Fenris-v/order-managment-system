package com.example.payment.enums

/**
 * Тип подтверждения платежа.
 */
enum class ConfirmationTypeEnum(val value: String) {
    /**
     * Подтверждение через редирект.
     */
    REDIRECT("redirect")
}
