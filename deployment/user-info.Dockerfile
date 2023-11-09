FROM openjdk:17
WORKDIR ./user-info-service
RUN ./gradlew build
COPY ./user-info-service/build/libs/user-info-service-0.0.1-SNAPSHOT.jar user-info.jar
ENTRYPOINT ["java", "-jar", "user-info.jar"]