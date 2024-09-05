package com.example.inventory.config.transaction

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Configuration
@ConditionalOnProperty(
    name = ["spring.data.mongodb.transactional-connection"],
    havingValue = "false",
    matchIfMissing = false
)
class TransactionConfig {
    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return ResourceLessTransactionManager()
    }

    @Bean
    fun transactionTemplate(transactionManager: PlatformTransactionManager): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }
}
