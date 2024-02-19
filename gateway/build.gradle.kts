import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example.gateway"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.0.3")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap:4.0.3")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.0.6")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.2")
    implementation("org.springframework.cloud:spring-cloud-config-client:4.0.2")

    implementation("org.springframework.boot:spring-boot-starter-security:3.1.2")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.postgresql:r2dbc-postgresql:1.0.2.RELEASE")
    implementation("org.springframework.data:spring-data-r2dbc:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.5")

    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.1.5")
    implementation("org.liquibase:liquibase-core:4.23.0")

    implementation("de.codecentric:spring-boot-admin-client:3.1.5")
    implementation("de.codecentric:spring-boot-admin-starter-client:3.1.5")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")

    implementation("io.micrometer:micrometer-tracing:1.2.2")
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.5")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.2.2")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:3.2.1")

    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
    implementation("io.projectreactor:reactor-test:3.5.8")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("gateway.jar")
}
