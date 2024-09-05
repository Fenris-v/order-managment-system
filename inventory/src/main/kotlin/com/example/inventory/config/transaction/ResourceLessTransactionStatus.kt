package com.example.inventory.config.transaction

import org.springframework.transaction.TransactionStatus

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

    override fun rollbackToSavepoint(savepoint: Any) {}

    override fun releaseSavepoint(savepoint: Any) {}

    override fun flush() {}

    override fun hasSavepoint(): Boolean {
        return false
    }
}
