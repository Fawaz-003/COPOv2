# Use Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Create app folder in container
WORKDIR /app

# Copy jar into the container
COPY target/copov2-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
