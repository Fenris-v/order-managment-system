package com.example.gateway.config

import com.example.gateway.AbstractApplicationTest
import com.example.gateway.config.filter.NonGlobalFilter
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.web.server.adapter.HttpWebHandlerAdapter
import org.springframework.web.server.handler.FilteringWebHandler
import org.springframework.web.server.handler.WebHandlerDecorator

@SpringBootTest
@Tag("integration-test")
class HttpHandlerConfigurationTest : AbstractApplicationTest() {
    @Autowired
    private lateinit var httpHandlerConfiguration: HttpHandlerConfiguration

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun testHttpHandlerCreation() {
        val handler: HttpHandler = httpHandlerConfiguration.httpHandler(context)
        assert(handler is HttpWebHandlerAdapter)

        val httpHandler = handler as HttpWebHandlerAdapter
        val chain: FilteringWebHandler = (httpHandler.delegate as WebHandlerDecorator).delegate as FilteringWebHandler
        val filters = chain.filters
        filters.forEach {
            assert(it !is NonGlobalFilter) { "Объекты реализующие NonGlobalFilter должны быть исключены из цепочки" }
        }
        println(chain.filters)
    }
}
