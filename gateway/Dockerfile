FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/gateway

WORKDIR /app/gateway

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/gateway/build/libs/gateway.jar .

CMD ["java", "-jar", "/app/gateway.jar"]
