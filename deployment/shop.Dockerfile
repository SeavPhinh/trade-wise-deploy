FROM openjdk:17
WORKDIR ./shop-service
RUN ./gradlew build
COPY ./shop-service/build/libs/shop-service-0.0.1-SNAPSHOT.jar shop.jar
ENTRYPOINT ["java", "-jar", "shop.jar"]