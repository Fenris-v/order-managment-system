package com.example.payment.event

import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

/**
 * Класс представляет событие платежа.
 */
class PaymentEvent(
    val paymentId: UUID,
    val delaySeconds: Long = 60,
    val expiryAt: LocalDateTime = LocalDateTime.now().plusHours(1)
) : Delayed {

    private val executeTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delaySeconds)

    /**
     * Метод возвращает оставшееся время до запуска события.
     */
    override fun getDelay(unit: TimeUnit): Long {
        val remainingTime = executeTime - System.currentTimeMillis()
        return unit.convert(remainingTime, TimeUnit.MILLISECONDS)
    }

    /**
     * Метод сравнивает текущее время с временем события.
     */
    override fun compareTo(other: Delayed): Int {
        return this.executeTime.compareTo((other as PaymentEvent).executeTime)
    }
}
