FROM gradle:8.11-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :storage-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/storage-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]