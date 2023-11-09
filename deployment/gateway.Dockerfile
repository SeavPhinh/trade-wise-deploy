FROM openjdk:17
COPY ./gateway-service/build/libs/gateway-service-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]