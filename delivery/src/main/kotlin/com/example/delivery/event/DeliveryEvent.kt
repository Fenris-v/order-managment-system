package com.example.delivery.event

import java.util.UUID
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class DeliveryEvent(val orderId: UUID, delaySeconds: Long = Random.nextLong(5, 20)) : Delayed {
    private val executeTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delaySeconds)

    override fun getDelay(unit: TimeUnit): Long {
        val remainingTime = executeTime - System.currentTimeMillis()
        return unit.convert(remainingTime, TimeUnit.MILLISECONDS)
    }

    override fun compareTo(other: Delayed): Int {
        return this.executeTime.compareTo((other as DeliveryEvent).executeTime)
    }
}
