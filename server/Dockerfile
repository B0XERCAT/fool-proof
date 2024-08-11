FROM openjdk:17-jdk-alpine AS base

# Run application as a non-root user.
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

# WORKDIR /server

FROM base AS gradle-setup
COPY ./gradle gradle
COPY ./build.gradle build.gradle
COPY ./settings.gradle setting.gradle
COPY ./gradlew gradlew
COPY ./src/test src/test
COPY ./src/main src/main
RUN chmod +x gradlew
RUN ./gradlew clean build

FROM base
COPY --from=gradle-setup build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
