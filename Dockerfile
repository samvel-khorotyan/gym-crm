# ---------- Build Stage ----------
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package spring-boot:repackage -DskipTests -Dmaven.test.skip=true

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Basic tools (CA certificates for HTTPS connections)
RUN apk add --no-cache ca-certificates

EXPOSE 8080

# Simple entrypoint - Spring Boot will use environment variables
ENTRYPOINT ["java", "-jar", "app.jar"]
