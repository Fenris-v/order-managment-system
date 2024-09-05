package com.example.inventory.config.transaction

import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus

class ResourceLessTransactionManager : PlatformTransactionManager {
    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        return ResourceLessTransactionStatus()
    }

    override fun commit(status: TransactionStatus) {}

    override fun rollback(status: TransactionStatus) {}
}
