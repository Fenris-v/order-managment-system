FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./starter-utils /app/starter-utils
COPY ./gradlew /app/gradlew
COPY ./gradle /app/gradle

COPY ./delivery /app/delivery

WORKDIR /app/delivery

RUN ../gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/delivery/build/libs/delivery.jar .

CMD ["java", "-jar", "/app/delivery.jar"]
