#!/bin/bash
# Build script for Vercel deployment

echo "Building Spring Boot application for Vercel..."

# Clean and package the application
mvn clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR file created at: target/copov2-0.0.1-SNAPSHOT.jar"
else
    echo "Build failed!"
    exit 1
fi
