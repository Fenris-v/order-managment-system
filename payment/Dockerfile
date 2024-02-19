FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/payment

WORKDIR /app/payment

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/payment/build/libs/payment.jar .

CMD ["java", "-jar", "/app/payment.jar"]
