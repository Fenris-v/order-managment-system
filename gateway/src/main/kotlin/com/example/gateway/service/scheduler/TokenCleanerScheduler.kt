package com.example.gateway.service.scheduler

import com.example.gateway.repository.RefreshTokenRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log: KLogger = KotlinLogging.logger {}

/**
 * Класс для регулярной очистки устаревших токенов.
 */
@Component
class TokenCleanerScheduler(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val databaseClient: DatabaseClient
) {
    /**
     * Метод для очистки устаревших Access и Refresh токенов. Вызывается каждый час.
     */
    @Scheduled(cron = "0 0 * * * *")
    fun clearTokens() {
        val now = LocalDateTime.now()
        refreshTokenRepository.deleteAllByExpireAtBefore(now).block()
        val sql = "DELETE FROM access_tokens at WHERE expire_at < :expireAt AND " +
                "NOT EXISTS(SELECT * FROM refresh_tokens rt WHERE at.id = rt.access_id)"
        databaseClient.sql(sql).bind("expireAt", now).fetch().one().block()
        log.info { "Токены очищены" }
    }
}
