FROM openjdk:17-jdk-slim AS base

# Run application as a non-root user.
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

RUN mkdir /server
WORKDIR /server

COPY ./gradlew gradlew
COPY ./gradle gradle
COPY ./build.gradle build.gradle
COPY ./settings.gradle setting.gradle
COPY ./src/test src/test
COPY ./src/main src/main
RUN chmod +x gradlew

EXPOSE 8080

COPY ./entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh
ENTRYPOINT [ "./entrypoint.sh" ]
