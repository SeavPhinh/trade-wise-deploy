FROM openjdk:17
WORKDIR ./chat-service
RUN gradle build
COPY ./chat-service/build/libs/chat-service-0.0.1-SNAPSHOT.jar chat.jar
ENTRYPOINT ["java", "-jar", "chat.jar"]