FROM openjdk:17
RUN ./gradlew build
COPY ./product-service/build/libs/product-service-0.0.1-SNAPSHOT.jar product.jar
ENTRYPOINT ["java", "-jar", "product.jar"]