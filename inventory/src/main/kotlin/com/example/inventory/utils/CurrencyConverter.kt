package com.example.inventory.utils

class CurrencyConverter {
    companion object {
        fun usdToRub(amount: Double): Double {
            return amount * 100
        }
    }
}
