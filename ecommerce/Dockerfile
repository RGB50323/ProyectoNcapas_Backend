FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=build /app/target/ecommerce-0.0.1-SNAPSHOT.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
