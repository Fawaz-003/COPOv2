# Step 1: Use Maven to build the project
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the JAR file (skipping tests for faster build)
RUN mvn clean package -DskipTests

# Step 2: Use lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy built JAR from the build stage
COPY --from=build /app/target/copov2-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
