FROM openjdk:17 AS builder
WORKDIR ./user-service
RUN ./gradlew build
FROM openjdk:17
COPY ./user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]