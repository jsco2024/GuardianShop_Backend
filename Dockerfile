FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
