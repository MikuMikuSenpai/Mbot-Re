FROM eclipse-temurin:23-jdk-alpine AS builder

RUN apk add --no-cache maven
WORKDIR /build

COPY ./pom.xml .
COPY ./src ./src

RUN mvn package

FROM eclipse-temurin:23-jdk-alpine

WORKDIR /mbot-directory
COPY --from=builder /build/target/*-jar-with-dependencies.jar ./bot.jar
CMD ["java", "-jar", "bot.jar"]
