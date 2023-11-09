FROM openjdk:17
WORKDIR ./category-service
RUN ./gradlew build
COPY ./category-service/build/libs/category-service-0.0.1-SNAPSHOT.jar category.jar
ENTRYPOINT ["java", "-jar", "category.jar"]