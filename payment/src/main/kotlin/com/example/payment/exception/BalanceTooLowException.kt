package com.example.payment.exception

/**
 * Исключение, связанное с недостаточным балансом.
 */
class BalanceTooLowException : RuntimeException(MESSAGE) {

    companion object {
        private const val MESSAGE: String = "Недостаточно средств на балансе"
    }
}
