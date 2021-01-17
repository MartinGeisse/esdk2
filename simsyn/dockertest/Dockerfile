FROM gradle:6.8.0-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/esdk2
WORKDIR /home/gradle/esdk2/mahdl
RUN ls -al
RUN gradle clean build buildPlugin publishToMavenLocal --no-daemon
WORKDIR /home/gradle/esdk2/simsyn
RUN gradle clean build --no-daemon

FROM openjdk:8-jdk-slim
RUN mkdir /app
COPY --from=build /home/gradle/simsyn/build/libs/*.jar /app
ENTRYPOINT ["java", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/app/dockertest.jar"]
