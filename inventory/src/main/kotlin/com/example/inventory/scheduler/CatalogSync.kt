package com.example.inventory.scheduler

import com.example.inventory.utils.sync.CatalogSyncUtil
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Компонент для синхронизации каталога.
 */
@Component
class CatalogSync(private val context: ApplicationContext) {
    /**
     * Выполняет синхронизацию каталога в заданный период.
     */
    @Scheduled(cron = "0 0 2 */7 * 1")
    fun sync() {
        context.getBean(CatalogSyncUtil::class.java).sync()
    }
}

