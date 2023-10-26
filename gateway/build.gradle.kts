import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example"
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

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")

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
