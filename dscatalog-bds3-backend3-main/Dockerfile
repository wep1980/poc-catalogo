FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY ./target/dscatalog-0.0.1-SNAPSHOT.jar dscatalog.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "dscatalog.jar"]
