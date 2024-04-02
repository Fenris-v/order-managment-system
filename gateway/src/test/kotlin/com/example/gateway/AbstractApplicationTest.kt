package com.example.gateway

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Tag("integration-test")
abstract class AbstractApplicationTest {
    companion object {
        const val TEST_USER_USERNAME = "test-user@mail.ru"
        const val TEST_USER_PASSWORD = "password"

        private val postgresImage = DockerImageName.parse("postgres")
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(postgresImage).withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url", Companion::r2dbcUrl)
            registry.add("spring.r2dbc.username", postgres::getUsername)
            registry.add("spring.r2dbc.password", postgres::getPassword)

            registry.add("spring.liquibase.url", postgres::getJdbcUrl)
            registry.add("spring.liquibase.user", postgres::getUsername)
            registry.add("spring.liquibase.password", postgres::getPassword)
        }

        fun r2dbcUrl(): String {
            return "r2dbc:postgresql://${postgres.host}:${postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${postgres.databaseName}"
        }

        @JvmStatic
        @BeforeAll
        internal fun setUp() {
            postgres.start()
        }
    }
}
