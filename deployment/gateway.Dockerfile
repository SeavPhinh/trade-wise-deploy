FROM eclipse-temurin:20-jdk-jammy
ADD ../gateway-service/build/libs/gateway-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]