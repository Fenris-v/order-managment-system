package com.example.inventory.scheduler

import com.example.inventory.utils.sync.CategorySyncUtil
import com.example.inventory.utils.sync.ProductSyncUtil
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ProductSync(private val categorySyncUtil: CategorySyncUtil, private val productSyncUtil: ProductSyncUtil) {
    @Scheduled(cron = "0 45 2 * * *")
    fun sync() {
        categorySyncUtil.syncCategories()
            .then(Mono.defer { productSyncUtil.syncProducts() })
            .block()
    }
}

