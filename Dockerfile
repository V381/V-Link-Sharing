FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/v-link-sharing-app-0.0.1-SNAPSHOT-standalone.jar /v-link-sharing-app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/v-link-sharing-app/app.jar"]
