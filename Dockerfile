
# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# Stage 2: Run the application on a smaller JRE image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/*.jar /app/app.jar

EXPOSE 8080

# Render provides PORT; local Docker defaults to 8080.
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -jar /app/app.jar --server.port=${PORT:-8080}"]
