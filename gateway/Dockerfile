FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./starter-utils /app/starter-utils
COPY ./gradlew /app/gradlew
COPY ./gradle /app/gradle
COPY ./proto /app/proto

COPY ./gateway /app/gateway

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/gateway/build/libs/gateway.jar .

CMD ["java", "-jar", "/app/gateway.jar"]
