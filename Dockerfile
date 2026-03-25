# Step 1: Build stage using Maven + JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Step 2: Run stage using lightweight JDK 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the generated JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]