FROM openjdk:17
COPY ./post-service/build/libs/post-service-0.0.1-SNAPSHOT.jar post.jar
ENTRYPOINT ["java", "-jar", "post.jar"]