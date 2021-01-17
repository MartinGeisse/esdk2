FROM gradle:6.8.0-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/esdk2
WORKDIR /home/gradle/esdk2/mahdl
RUN gradle clean build buildPlugin publishToMavenLocal --no-daemon
WORKDIR /home/gradle/esdk2/simsyn
RUN gradle clean build --no-daemon

FROM openjdk:8-jdk-slim
WORKDIR /
COPY --from=build /home/gradle/esdk2/simsyn/dockertest/build/distributions/dockertest-0.1.tar /app.tar
RUN tar -xf app.tar
RUN rm /app.tar
RUN mv dockertest-0.1 app

WORKDIR /app/bin
ENTRYPOINT ["./dockertest"]
# ENTRYPOINT ["./java", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/app/dockertest.jar"]
