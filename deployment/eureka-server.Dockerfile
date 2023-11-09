FROM openjdk:17 AS builder
WORKDIR ./eureka.server
RUN ./gradlew build
FROM openjdk:17
COPY ./eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "eureka-server-0.0.1-SNAPSHOT.jar"]

