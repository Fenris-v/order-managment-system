package com.example.inventory.exception

class ReservedException : RuntimeException(MESSAGE) {
    companion object {
        private const val MESSAGE = "Недостаточно товара на складе"
    }
}
