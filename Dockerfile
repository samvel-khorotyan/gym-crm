# ---------- Build Stage ----------
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package spring-boot:repackage -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Disabled integrations
ENV DB_URL=jdbc:mysql://disabled:3306/disabled
ENV DB_USERNAME=disabled
ENV DB_PASSWORD=disabled
ENV SPRING_ACTIVEMQ_BROKER_URL=tcp://disabled:61616
ENV SPRING_ACTIVEMQ_USER=disabled
ENV SPRING_ACTIVEMQ_PASSWORD=disabled
ENV EUREKA_CLIENT_ENABLED=false

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Dspring.profiles.active=local", \
  "-Dspring.datasource.url=jdbc:disabled", \
  "-Dspring.flyway.enabled=false", \
  "-Dspring.jpa.hibernate.ddl-auto=none", \
  "-jar", "app.jar"]
