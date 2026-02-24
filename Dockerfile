# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY checkstyle.xml .

RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /app/target/scubakeep-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]