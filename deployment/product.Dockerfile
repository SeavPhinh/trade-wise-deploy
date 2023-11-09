FROM openjdk:17
WORKDIR ./product-service
RUN ./gradlew build
COPY ./product-service/build/libs/product-service-0.0.1-SNAPSHOT.jar product.jar
ENTRYPOINT ["java", "-jar", "product.jar"]