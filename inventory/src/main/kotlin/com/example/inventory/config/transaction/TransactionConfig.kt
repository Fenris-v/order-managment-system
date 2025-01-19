package com.example.inventory.config.transaction

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

/**
 * Конфигурационный класс для настройки транзакций в MongoDB. Данная конфигурация применяется только в случае,
 * если в конфигурации приложения задано значение `false` для свойства `spring.data.mongodb.transactional-connection`.
 */
@Configuration
@ConditionalOnProperty(
    name = ["spring.data.mongodb.transactional-connection"],
    havingValue = "false",
    matchIfMissing = false
)
class TransactionConfig {

    /**
     * Создает и возвращает менеджер транзакций.
     */
    @Bean
    @Primary
    fun transactionManager(): PlatformTransactionManager {
        return ResourceLessTransactionManager()
    }

    /**
     * Создает и возвращает шаблон транзакции.
     */
    @Bean
    fun transactionTemplate(transactionManager: PlatformTransactionManager): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }
}
