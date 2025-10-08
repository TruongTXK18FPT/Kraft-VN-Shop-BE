# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper and pom.xml first (for better caching)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV FILE_UPLOAD_DIR=/app/uploads
ENV FILE_BASE_URL=http://localhost:8080/api/files

# Run the application
CMD ["java", "-jar", "target/Kraft-0.0.1-SNAPSHOT.jar"]

