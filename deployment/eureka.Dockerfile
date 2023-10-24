FROM eclipse-temurin:20-jdk-jammy
ADD ../eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]