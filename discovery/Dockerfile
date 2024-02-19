FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . /app/discovery

WORKDIR /app/discovery

RUN ./gradlew clean build

FROM amd64/openjdk:17-alpine AS production

WORKDIR /app

COPY --from=builder /app/discovery/build/libs/discovery.jar .

CMD ["java", "-jar", "/app/discovery.jar"]
