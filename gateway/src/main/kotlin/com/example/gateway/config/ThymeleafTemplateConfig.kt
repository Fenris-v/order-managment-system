package com.example.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.nio.charset.StandardCharsets

/**
 * Конфигурация шаблонизатора Thymeleaf.
 */
@Configuration
class ThymeleafTemplateConfig {
    companion object {
        private const val PREFIX = "/templates/"
        private const val SUFFIX = ".html"
    }

    /**
     * Конфигурация шаблонизатора Thymeleaf.
     */
    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val springTemplateEngine = SpringTemplateEngine()
        springTemplateEngine.addTemplateResolver(emailTemplateResolver())

        return springTemplateEngine
    }

    private fun emailTemplateResolver(): ClassLoaderTemplateResolver {
        val emailTemplateResolver = ClassLoaderTemplateResolver()
        emailTemplateResolver.prefix = PREFIX
        emailTemplateResolver.suffix = SUFFIX
        emailTemplateResolver.templateMode = TemplateMode.HTML
        emailTemplateResolver.characterEncoding = StandardCharsets.UTF_8.name()
        emailTemplateResolver.isCacheable = false

        return emailTemplateResolver
    }
}
