FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/delivery

WORKDIR /app/delivery

RUN ./gradlew clean build

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/delivery/build/libs/delivery.jar .

CMD ["java", "-jar", "/app/delivery.jar"]
