FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/inventory

WORKDIR /app/inventory

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/inventory/build/libs/inventory.jar .

CMD ["java", "-jar", "/app/inventory.jar"]
