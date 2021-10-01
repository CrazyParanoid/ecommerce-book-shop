FROM openjdk:8-jdk-alpine
MAINTAINER com.max.tech
WORKDIR /opt/eureka-server/
EXPOSE 8761

RUN apk update && apk add curl

COPY target/eureka-server-1.0.jar eureka-server.jar
ENTRYPOINT ["java","-jar","eureka-server.jar"]