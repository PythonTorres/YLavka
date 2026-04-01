FROM openjdk:17.0.1-jdk-slim

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

# update packages and install dos2unix
RUN apt-get update && apt-get install -y dos2unix

# copy the gradle files to the container
COPY . /opt/app

# change to the app directory
WORKDIR /opt/app

COPY ${JAR_FILE} yandex-lavka.jar

# convert gradlew to unix format
RUN dos2unix gradlew

# build the application
RUN bash gradlew build -x test

RUN cp build/libs/*SNAPSHOT.jar ./yandex-lavka.jar

ENTRYPOINT ["java","-jar","yandex-lavka.jar"]
