# FCS_BE

Backend service for Fashion Consignment System (FCS), built with Spring Boot, Maven, and Java 21.

## Tech Stack
- Java 21
- Spring Boot 3
- Maven
- MySQL

## Project Structure
- `src/main/java/com/fcs/be/config`: cross-cutting configs (security, cors)
- `src/main/java/com/fcs/be/common`: shared responses and exception handling
- `src/main/java/com/fcs/be/modules/<feature>`: feature modules (`controller`, `service`, `repository`, `entity`, `dto`, `mapper`)
- `src/main/resources`: profile-based application configs
- `src/test/java/com/fcs/be`: integration and context tests

## Prerequisites
- JDK 21+
- Maven 3.9+
- MySQL (for dev profile, unless overriding env vars)

## Environment Variables
- `SPRING_PROFILES_ACTIVE` (default: `dev`)
- `SERVER_PORT` (default: `8080`)
- `APP_CORS_ALLOWED_ORIGINS` (default: `http://localhost:3000`)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

## Run Locally
```bash
mvn clean test
mvn spring-boot:run
```

## Build Artifact
```bash
mvn clean package
```

## Health Check
- Endpoint: `GET /api/v1/health`
