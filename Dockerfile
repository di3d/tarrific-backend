# =========================
# 1. BUILD STAGE
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set work directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# =========================
# 2. RUNTIME STAGE
# =========================
FROM eclipse-temurin:21-jdk-jammy

# Set app directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose backend port
EXPOSE 8080

# Optional: use environment variables for DB configuration
# ENV SPRING_DATASOURCE_URL=jdbc:mysql://mariadb:3306/tariff
# ENV SPRING_DATASOURCE_USERNAME=tarrific
# ENV SPRING_DATASOURCE_PASSWORD=tarrific123

# Run the Spring Boot app
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
