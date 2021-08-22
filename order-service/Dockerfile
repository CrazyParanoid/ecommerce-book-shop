FROM adoptopenjdk/openjdk16:alpine-jre
MAINTAINER com.max.tech
WORKDIR /opt/order-service/
EXPOSE 8082

RUN apk update && apk add curl

COPY target/order-service-1.0.jar order-service.jar
ENTRYPOINT ["java","-jar","order-service.jar"]