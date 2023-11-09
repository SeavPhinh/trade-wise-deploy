FROM openjdk:17 AS builder
WORKDIR ./product-service
RUN ./gradlew build
FROM openjdk:17
COPY ./product-service/build/libs/product-service-0.0.1-SNAPSHOT.jar product.jar
ENTRYPOINT ["java", "-jar", "product.jar"]