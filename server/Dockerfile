FROM openjdk:17-jdk-slim AS base

# Run application as a non-root user.
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

# WORKDIR /server

FROM base AS gradle-setup
COPY ./gradlew gradlew
COPY ./gradle gradle
COPY ./build.gradle build.gradle
COPY ./settings.gradle setting.gradle
COPY ./src/test src/test
COPY ./src/main src/main
RUN chmod +x gradlew
RUN ./gradlew clean build

FROM base
RUN mkdir /server
WORKDIR /server
COPY . .
RUN chmod +x gradlew
COPY --from=gradle-setup build/libs/*.jar app.jar

EXPOSE 8080

COPY ./entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh
ENTRYPOINT [ "./entrypoint.sh" ]
# ENTRYPOINT ["java","-jar","/app.jar"]
