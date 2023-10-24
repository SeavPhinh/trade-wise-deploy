FROM eclipse-temurin:20-jdk-jammy
ADD ../product-service/build/libs/product-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]