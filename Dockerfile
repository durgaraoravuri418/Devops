# ===========================
# 1. BUILD STAGE
# ===========================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom first and download dependencies (cache improvement)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Now copy the source code
COPY src ./src

# Build the application
RUN mvn -B package -DskipTests


# ===========================
# 2. RUNTIME STAGE
# ===========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the app port (optional)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]

