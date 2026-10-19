# ===== Build stage =====
FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY resource-processor/build.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY resource-processor/src ./src

RUN ./gradlew assemble --no-daemon -x test

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]