FROM openjdk:17
WORKDIR ./eureka.server
RUN ./gradlew build
COPY ./eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "eureka-server-0.0.1-SNAPSHOT.jar"]

