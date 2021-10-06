FROM adoptopenjdk/openjdk16:alpine-jre
MAINTAINER com.max.tech
WORKDIR /opt/api-gateway/
EXPOSE 8087

RUN apk update && apk add curl

COPY target/api-gateway-1.0.jar api-gateway.jar
ENTRYPOINT ["java","-jar","api-gateway.jar"]