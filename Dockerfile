# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache dependencies first (faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the jar
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Run ────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""
ENV JWT_SECRET=""
ENV JWT_EXPIRATION_MS=""

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]