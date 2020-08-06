FROM openjdk:11.0.2-jdk-slim-stretch
MAINTAINER Shaun McCready
EXPOSE 8080
ARG JAR_FILE=target/upgrade-campsite-java-api.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]