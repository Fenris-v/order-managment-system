package com.example.inventory.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackages = ["com.example.inventory.repository"])
class MongoConfig(private val mongoProperties: MongoProperties) : AbstractReactiveMongoConfiguration() {
    // TODO: Реплицирование отключено, но без него не работают транзакции
    override fun reactiveMongoClient(): MongoClient {
        val connectionString = String.format(
            // "mongodb://%s:%s@%s:%d/%s?authSource=%s&replicaSet=%s", // Вариант с указанием реплики
            "mongodb://%s:%s@%s:%d/%s?authSource=%s",
            mongoProperties.username,
            String(mongoProperties.password),
            mongoProperties.host,
            mongoProperties.port,
            mongoProperties.database,
            mongoProperties.authenticationDatabase,
            // mongoProperties.replicaSetName // Имя реплики
        )

        return MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(connectionString))
                .uuidRepresentation(mongoProperties.uuidRepresentation)
                .build()
        )
    }

    override fun getDatabaseName(): String {
        return mongoProperties.database
    }

    @Bean
    fun transactionManager(reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory?): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory!!)
    }
}
