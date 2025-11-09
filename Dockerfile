# =========================
# 1. BUILD STAGE
# =========================
FROM maven:3.9.6-eclipse-temurin-22 AS build

WORKDIR /app

# Copy POM first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# 2. RUNTIME STAGE
# =========================
FROM eclipse-temurin:22-jdk-jammy

WORKDIR /app

# Copy compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose app port
EXPOSE 8080

# Environment variables are injected by the platform (Render, Railway, etc.)
# No secrets baked into image

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
