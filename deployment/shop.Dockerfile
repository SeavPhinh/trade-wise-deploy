FROM openjdk:17 AS builder
WORKDIR ./shop-service
RUN ./gradlew build
FROM openjdk:17
COPY ./shop-service/build/libs/shop-service-0.0.1-SNAPSHOT.jar shop.jar
ENTRYPOINT ["java", "-jar", "shop.jar"]