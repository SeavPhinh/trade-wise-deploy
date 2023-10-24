FROM eclipse-temurin:20-jdk-jammy
ADD ../post-service/build/libs/post-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]