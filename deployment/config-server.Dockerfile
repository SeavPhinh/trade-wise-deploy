FROM eclipse-temurin:20-jdk-jammy
ADD ../config-server/build/libs/config-server-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "root.jar"]