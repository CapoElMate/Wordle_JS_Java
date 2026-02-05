# Use a Java base image
FROM openjdk:25-ea-21-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle project files
COPY . .

# Build the application using Gradle's bootJar task
RUN ./gradlew bootJar --no-daemon

# Use a new stage for the final, lightweight image
FROM openjdk:25-ea-21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]