FROM openjdk:17
COPY ../eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar eureka.jar
ENTRYPOINT ["java", "-jar", "eureka.jar"]