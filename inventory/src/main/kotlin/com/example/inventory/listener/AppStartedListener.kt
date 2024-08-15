package com.example.inventory.listener

import com.example.inventory.repository.CategoryRepository
import com.example.inventory.repository.ProductRepository
import com.example.inventory.scheduler.CatalogSync
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Слушатель события старта приложения.
 */
@Component
class AppStartedListener(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val catalogSync: CatalogSync
) : ApplicationListener<ApplicationReadyEvent> {
    /**
     * Листенер при старте приложения, который проверяет наполненность каталога и запускает синхронизацию в случае
     * необходимости.
     *
     * @param event Событие старта приложения.
     */
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        Mono.zip(categoryRepository.existsBy(), productRepository.existsBy())
            .flatMap { tuple ->
                if (!tuple.t1 || !tuple.t2) Mono.just(catalogSync.sync())
                else Mono.empty()
            }
            .subscribe()
    }
}
