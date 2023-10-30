FROM openjdk:17
COPY ../user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar user.jar
ENTRYPOINT ["java", "-jar", "user.jar"]