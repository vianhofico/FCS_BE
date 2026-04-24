FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/fcs-be-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -Dserver.port=${SERVER_PORT} -jar /app/app.jar"]
