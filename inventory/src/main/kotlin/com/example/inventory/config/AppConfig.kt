package com.example.inventory.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Конфигурационный класс приложения.
 */
@Configuration
@EnableScheduling
class AppConfig {
    /**
     * Экземпляр [ModelMapper].
     *
     * @return [ModelMapper]
     */
    @Bean
    fun modelMapper(): ModelMapper {
        return ModelMapper()
    }
}
