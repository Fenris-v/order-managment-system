import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example.payment"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val versionCatalog = project.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2") // todo
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2") // todo

    versionCatalog.findBundle("spring").ifPresent { implementation(it) }
    versionCatalog.findBundle("postgres").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springValidation").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springJpa").ifPresent { implementation(it) }

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
    archiveFileName.set("payment.jar")
}
