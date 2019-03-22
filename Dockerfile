# https://github.com/keeganwitt/docker-gradle/blob/fcc9237909222c76c8d22d5e7f6183b9af4e58b6/jdk11/Dockerfile
FROM gradle:5.3.0-jdk11

COPY build.gradle settings.gradle gradlew /app/
COPY src /app/src
COPY gradle /app/gradle

USER root
RUN chown -R 1000:1000 /app

USER 1000
WORKDIR /app

ENV GRADLE_USER_HOME /home/gradle/gradle-cache
RUN gradle compileGroovy
