# 1. Use Maven to build the project
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (for faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire project and build it
COPY . .
RUN mvn clean package -DskipTests

# 2. Run the built JAR with a slim JDK image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy only the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app listens on (Render/Fly.io will map it dynamically)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
