FROM openjdk:17
WORKDIR /app
COPY *.jar app.jar
EXPOSE 11002
ENTRYPOINT ["java", "-jar", "/app/app.jar"]