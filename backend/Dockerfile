# ---------- Stage 1: Build the Spring Boot application ----------
  FROM maven:3.9.6-eclipse-temurin-17 AS build
 
  # Set working directory inside the build container
  WORKDIR /app
 
  # Copy the entire project (including pom.xml and src/)
  COPY . .
 
  # Build the project and skip tests (adjust if you want to include tests)
  RUN mvn clean package -DskipTests
 
 
  # ---------- Stage 2: Run the application ----------
  FROM eclipse-temurin:17-jre
 
  # Set working directory inside the runtime container
  WORKDIR /app
 
  # Create a non-root user for security
  #RUN addgroup -S spring && adduser -S spring -G spring
  RUN groupadd spring && useradd -g spring spring
 
  # Copy the built JAR from the build stage (wildcard allows flexible naming)
  COPY --from=build /app/target/*.jar app.jar
 
  # Assign ownership to the non-root user
  RUN chown spring:spring app.jar
 
  # Switch to the non-root user
  USER spring
 
  # Expose application port
  EXPOSE 8080
 
  # Health check (ensure Spring Boot actuator is enabled)
  HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
 
  # Run the JAR file
  ENTRYPOINT ["java", "-jar", "app.jar"]
 