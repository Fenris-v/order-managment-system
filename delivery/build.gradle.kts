import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example.delivery"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val versionCatalog = project.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":starter-utils"))

    versionCatalog.findBundle("spring").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springKafka").ifPresent { implementation(it) }

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
    archiveFileName.set("delivery.jar")
}
