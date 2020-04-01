FROM openjdk:11-jdk-slim
ADD target/GlobalTerrorismAPI-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD java -jar GlobalTerrorismAPI-0.0.1-SNAPSHOT.jar