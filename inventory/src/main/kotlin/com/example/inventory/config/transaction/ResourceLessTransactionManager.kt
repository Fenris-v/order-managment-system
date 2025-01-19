package com.example.inventory.config.transaction

import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus

/**
 * Менеджер транзакций для работы с MongoDB без транзакций.
 */
class ResourceLessTransactionManager : PlatformTransactionManager {
    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        return ResourceLessTransactionStatus()
    }

    override fun commit(status: TransactionStatus) {
        // no-op
    }

    override fun rollback(status: TransactionStatus) {
        // no-op
    }
}
