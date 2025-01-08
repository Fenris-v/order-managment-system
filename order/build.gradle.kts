import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example.order"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val versionCatalog = project.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.2")
    implementation("org.springframework.kafka:spring-kafka:3.2.4")
    implementation("org.modelmapper:modelmapper:3.2.1")

    implementation(project(":starter-utils")) // TODO: заменить на nexus

    versionCatalog.findBundle("spring").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springValidation").ifPresent { implementation(it) }

    versionCatalog.findLibrary("swagger").ifPresent { implementation(it) }

    versionCatalog.findBundle("monitoring").ifPresent { implementation(it) }
    versionCatalog.findBundle("logs").ifPresent { implementation(it) }
    versionCatalog.findLibrary("kotlinReflect").ifPresent { implementation(it) }

    versionCatalog.findBundle("test").ifPresent { implementation(it) }
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
    archiveFileName.set("order.jar")
}
