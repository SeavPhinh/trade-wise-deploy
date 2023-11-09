FROM openjdk:17 AS builder
WORKDIR ./category-service
RUN ./gradlew build
FROM openjdk:17
COPY ./category-service/build/libs/category-service-0.0.1-SNAPSHOT.jar category.jar
ENTRYPOINT ["java", "-jar", "category.jar"]