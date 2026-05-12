# ─────────────────────────────────────────────
#  Dockerfile – Spring Boot AI Chatbot (Groq)
#  Multi-stage: build JAR → slim runtime image
# ─────────────────────────────────────────────

# ── Stage 1: Build ────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Cache Maven dependencies (only re-runs if pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build the JAR
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy built JAR
COPY --from=builder /app/target/*.jar app.jar

# Create log directory
RUN mkdir -p logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]