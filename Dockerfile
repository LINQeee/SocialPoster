FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/social_poster-0.0.1-RELEASE.jar app.jar

CMD ["java", "-jar", "app.jar"]