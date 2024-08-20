import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val versionCatalog = project.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example.discovery"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    versionCatalog.findLibrary("springConfigServer").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springEurekaServer").ifPresent { implementation(it) }
    versionCatalog.findBundle("monitoring").ifPresent { implementation(it) }
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
    archiveFileName.set("discovery.jar")
}
