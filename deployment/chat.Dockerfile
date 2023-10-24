FROM eclipse-temurin:20-jdk-jammy
ADD ../chat-service/build/libs/chat-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]