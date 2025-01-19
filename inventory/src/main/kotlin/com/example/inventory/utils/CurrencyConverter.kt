package com.example.inventory.utils

/**
 * Класс-хелпер для работы с конвертацией валют.
 */
class CurrencyConverter {

    companion object {

        /**
         * Фейковый конвертер валют. Конвертирует доллары в рубли и возвращает целое число.
         */
        fun usdToRub(amount: Double): Int {
            return (amount * 100).toInt()
        }
    }
}
