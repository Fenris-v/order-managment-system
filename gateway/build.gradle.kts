import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.serialization") version "2.0.0"

    id("com.google.protobuf") version "0.9.4"
}

group = "com.example.gateway"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val versionCatalog = project.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":starter-utils")) // TODO: заменить на nexus
//    versionCatalog.findLibrary("starterUtils").ifPresent { implementation(it) }

    versionCatalog.findLibrary("swagger").ifPresent { implementation(it) }

    versionCatalog.findLibrary("serialization").ifPresent { implementation(it) }
    versionCatalog.findLibrary("kotlinReflect").ifPresent { implementation(it) }

    versionCatalog.findBundle("mailing").ifPresent { implementation(it) }

    versionCatalog.findBundle("spring").ifPresent { implementation(it) }
    versionCatalog.findBundle("grpc").ifPresent { implementation(it) }
    versionCatalog.findLibrary("grpcServer").ifPresent { implementation(it) }
    versionCatalog.findBundle("jwt").ifPresent { implementation(it) }

    versionCatalog.findLibrary("springSecurity").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springGateway").ifPresent { implementation(it) }

    versionCatalog.findBundle("postgres").ifPresent { implementation(it) }

    versionCatalog.findLibrary("springJpa").ifPresent { implementation(it) }
    versionCatalog.findLibrary("springValidation").ifPresent { implementation(it) }

    versionCatalog.findLibrary("caffeine").ifPresent { implementation(it) }

//    versionCatalog.findBundle("monitoring").ifPresent { implementation(it) }
    versionCatalog.findBundle("logs").ifPresent { implementation(it) }

    versionCatalog.findBundle("test").ifPresent { testImplementation(it) }
}

tasks.withType<KotlinCompile> {
    dependsOn("generateProto")
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("gateway.jar")
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
