FROM eclipse-temurin:20-jdk-jammy
ADD ../category-service/build/libs/category-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]