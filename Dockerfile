# Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
# Run stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
RUN mkdir -p uploads data
COPY --from=build /target/smart-vehicle-procurement-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
