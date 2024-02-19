FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/general

WORKDIR /app/general

RUN ./gradlew clean build -x test

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/general/build/libs/general.jar .

CMD ["java", "-jar", "/app/general.jar"]
