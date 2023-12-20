package com.example.gateway.service.scheduler

import com.example.gateway.repository.BlacklistTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TokenCleanerScheduler(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val blacklistTokenRepository: BlacklistTokenRepository
) {
    @Scheduled(cron = "0 0 * * * *")
    fun clearBlacklist() {
        blacklistTokenRepository.deleteAllByExpireAtBefore().block()
    }

    @Scheduled(cron = "0 0 * * * *")
    fun clearRefreshList() {
        refreshTokenRepository.deleteAllByExpireAtBefore().block()
    }
}
