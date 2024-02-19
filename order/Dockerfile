FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/order

WORKDIR /app/order

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/order/build/libs/order.jar .

CMD ["java", "-jar", "/app/order.jar"]
