package com.example.inventory.config.transaction

import org.springframework.transaction.TransactionStatus

/**
 * Класс для настройки статусов MongoDB при работе без транзакций.
 */
class ResourceLessTransactionStatus : TransactionStatus {
    override fun isNewTransaction(): Boolean {
        return false
    }

    override fun setRollbackOnly() {}

    override fun isRollbackOnly(): Boolean {
        return false
    }

    override fun isCompleted(): Boolean {
        return false
    }

    override fun createSavepoint(): Any {
        return Any()
    }

    override fun rollbackToSavepoint(savepoint: Any) {
        // no-op
    }

    override fun releaseSavepoint(savepoint: Any) {
        // no-op
    }

    override fun flush() {
        // no-op
    }

    override fun hasSavepoint(): Boolean {
        return false
    }
}
