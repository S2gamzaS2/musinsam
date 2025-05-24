FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY *.jar app.jar
EXPOSE 11001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]