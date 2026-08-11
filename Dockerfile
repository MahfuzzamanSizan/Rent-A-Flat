# ---- Stage 1: Build the application ----
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy Gradle files first (better layer caching)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew .
RUN chmod +x gradlew

# Download dependencies (cached layer if build.gradle doesn't change)
RUN ./gradlew dependencies --no-daemon || true

# Now copy source and build
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]