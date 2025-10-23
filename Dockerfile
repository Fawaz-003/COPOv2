# =====================
# Step 1: Build Stage
# =====================
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the JAR (skip tests for faster build)
RUN mvn clean package -DskipTests

# =====================
# Step 2: Run Stage
# =====================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot will bind to
# Render injects PORT env variable, default fallback 9091
EXPOSE 9091

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
