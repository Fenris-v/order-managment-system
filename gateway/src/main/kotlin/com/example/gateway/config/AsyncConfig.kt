package com.example.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

/**
 * Класс конфигураций для настройки выполнения асинхронных задач.
 */
@EnableAsync
@Configuration
class AsyncConfig {
    /**
     * Создаёт ThreadPoolTaskExecutor для асинхронных задач связанных с отправкой писем.
     *
     * @return Экземпляр ThreadPoolTaskExecutor.
     */
    @Bean
    fun asyncMailExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setThreadNamePrefix("asyncMailThread-")
        executor.initialize()

        return executor
    }
}
