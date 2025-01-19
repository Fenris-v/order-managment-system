package com.example.inventory.exception

/**
 * Исключение, выбрасываемое при попытке зарезервировать больше товара, чем есть на складе
 */
class ReservedException : RuntimeException(MESSAGE) {

    companion object {
        private const val MESSAGE = "Недостаточно товара на складе"
    }
}
