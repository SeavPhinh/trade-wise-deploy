FROM openjdk:17
RUN gradle build
COPY ./user-info-service/build/libs/user-info-service-0.0.1-SNAPSHOT.jar user-info.jar
ENTRYPOINT ["java", "-jar", "user-info.jar"]