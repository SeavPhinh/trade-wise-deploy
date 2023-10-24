FROM eclipse-temurin:20-jdk-jammy
ADD ../user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]