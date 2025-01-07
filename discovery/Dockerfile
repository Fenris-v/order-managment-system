FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY ./discovery /app/discovery
COPY ./gradle/libs.versions.toml /app/discovery/gradle/libs.versions.toml

WORKDIR /app/discovery

RUN ./gradlew clean build

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY ./config ./config
COPY --from=builder /app/discovery/build/libs/discovery.jar .
EXPOSE 8080

CMD ["java", "-jar", "/app/discovery.jar"]
