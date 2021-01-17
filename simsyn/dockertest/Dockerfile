FROM gradle:6.8.0-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/simsyn
WORKDIR /home/gradle/simsyn
RUN gradle build --no-daemon

FROM openjdk:8-jdk-slim
RUN mkdir /app
COPY --from=build /home/gradle/simsyn/build/libs/*.jar /app
ENTRYPOINT ["java", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/app/dockertest.jar"]
