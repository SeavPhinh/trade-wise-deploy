FROM eclipse-temurin:20-jdk-jammy
ADD ../user-info-service/build/libs/user-info-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]