# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-alpine

# instalar curl para healthcheck
RUN apk add --no-cache curl

ARG APP_VERSION
ARG BUILD_TIME

ENV APP_VERSION=$APP_VERSION
ENV BUILD_TIME=$BUILD_TIME

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
CMD curl -f http://localhost:8080/actuator/health || exit 1

