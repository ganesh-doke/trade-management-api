# Stage 1: Build stage using Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/project

# Copy only the configuration files first to leverage Docker cache
COPY build.gradle settings.gradle ./
# Copy the source code
COPY src ./src

# Build the application (skipping tests for speed in CI/CD)
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Security: Create a non-root user to run the app
RUN addgroup --system javauser && adduser --system javauser --ingroup javauser
USER javauser

# Copy the executable JAR from the build stage
# Note: Ensure the path matches your project name (e.g., build/libs/your-api-0.0.1.jar)
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Expose the standard port (default 8080 for Spring Boot)
EXPOSE 8080

# Execute the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
