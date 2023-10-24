FROM eclipse-temurin:20-jdk-jammy
ADD ../shop-service/build/libs/shop-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]