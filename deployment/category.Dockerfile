FROM openjdk:17
COPY ./category-service/build/libs/category-service-0.0.1-SNAPSHOT.jar category.jar
ENTRYPOINT ["java", "-jar", "category.jar"]