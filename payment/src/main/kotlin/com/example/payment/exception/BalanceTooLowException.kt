package com.example.payment.exception

class BalanceTooLowException : RuntimeException(MESSAGE) {
    companion object {
        private const val MESSAGE: String = "Недостаточно средств на балансе"
    }
}
