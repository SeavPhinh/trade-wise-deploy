FROM openjdk:17
COPY ../config-server/build/libs/config-server-0.0.1-SNAPSHOT.jar configServer.jar
ENTRYPOINT ["java", "-jar", "configServer.jar"]