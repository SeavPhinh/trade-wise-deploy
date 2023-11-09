FROM openjdk:17
RUN ./gradlew build
COPY ./user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]