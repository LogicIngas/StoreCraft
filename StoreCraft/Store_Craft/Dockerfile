# =============================================================================
# Multi-Stage Dockerfile for StoreCraft Spring Boot Backend
# Build stage : maven:3.9.6-eclipse-temurin-21
# Runtime stage: eclipse-temurin:21-jre-jammy
# =============================================================================

# ---- Stage 1: Build --------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy dependency manifests first for layer caching
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source and build the fat JAR
COPY src/ src/
RUN mvn package -DskipTests -B

# ---- Stage 2: Runtime -------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Non-root user for security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy only the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Uploads directory (mount a persistent volume here in production)
RUN mkdir -p /var/data/uploads && chown -R appuser:appgroup /var/data/uploads

USER appuser

EXPOSE 8080

# Production JVM tuning: container-aware heap, avoid class data sharing issues
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
