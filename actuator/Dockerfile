FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY ./actuator /app/actuator

WORKDIR /app/actuator

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/actuator/build/libs/admin.jar .

CMD ["java", "-jar", "/app/admin.jar"]
