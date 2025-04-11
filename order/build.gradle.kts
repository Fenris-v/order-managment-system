import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"

    id("com.google.protobuf") version "0.9.4"
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
    implementation(project(":starter-utils"))

    versionCatalog.findBundle("grpc").ifPresent { implementation(it) }
    versionCatalog.findLibrary("grpcClient").ifPresent { implementation(it) }

    versionCatalog.findBundle("spring").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springValidation").ifPresent { implementation(it) }

    versionCatalog.findLibrary("springKafka").ifPresent { implementation(it) }
    versionCatalog.findLibrary("mongo").ifPresent { implementation(it) }
    versionCatalog.findLibrary("modelmapper").ifPresent { implementation(it) }

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

tasks.withType<KotlinCompile> {
    dependsOn("generateProto")
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.27.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.65.1"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("../proto")
        }
    }
}
