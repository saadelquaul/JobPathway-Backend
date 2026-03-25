# ==================================
# Build stage
# ==================================
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml first (leverage Docker cache)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw package -DskipTests -B

# ==================================
# Run stage
# ==================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create uploads directory
RUN mkdir -p uploads/resumes

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set active profile to prod
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
