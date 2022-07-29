FROM openjdk:8
ADD target/conference-room-api.war app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]