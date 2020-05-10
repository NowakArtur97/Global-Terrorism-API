FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/GlobalTerrorismAPI-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} global_terrorismAPI.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/global_terrorismAPI.jar"]