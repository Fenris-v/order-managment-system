package com.example.inventory.utils.sync

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Класс для синхронизации каталога.
 */
@Lazy
@Component
class CatalogSyncUtil(private val categorySyncUtil: CategorySyncUtil, private val productSyncUtil: ProductSyncUtil) {

    /**
     * Метод для синхронизации каталога.
     */
    fun sync() {
        categorySyncUtil.syncCategories()
            .then(Mono.defer { productSyncUtil.syncProducts() })
            .subscribe()
    }
}
