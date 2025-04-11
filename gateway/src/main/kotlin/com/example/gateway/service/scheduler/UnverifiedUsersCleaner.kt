package com.example.gateway.service.scheduler

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log: KLogger = KotlinLogging.logger {}

/**
 * Класс для регулярной очистки неподтвержденных пользователей.
 */
@Component
class UnverifiedUsersCleaner(private val databaseClient: DatabaseClient) {

    /**
     * Метод для очистки неподтвержденных пользователей. Вызывается каждый час.
     */
    @Scheduled(cron = "0 0 * * * *")
    fun clearUnverifiedUsers() {
        val sql = "DELETE FROM users WHERE verified_at IS NULL AND created_at <= :date"
        databaseClient.sql(sql).bind("date", LocalDateTime.now().minusDays(1)).fetch().one().block()
        log.info { "Не верифицированные пользователи удалены" }
    }
}
