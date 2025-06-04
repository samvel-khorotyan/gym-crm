# ---------- Build Stage ----------
FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /app
COPY . .

RUN mvn clean package spring-boot:repackage -DskipTests \
    -Dspring-boot.repackage.mainClass=com.gymcrm.GymCRMApplication

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN ls -la app.jar && [ -s app.jar ]

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]

CMD ["--spring.main.allow-bean-definition-overriding=true"]