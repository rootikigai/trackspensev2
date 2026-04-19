FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/trackspensev2-0.0.1-SNAPSHOT.jar trackspensev2.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "trackspensev2.jar"]