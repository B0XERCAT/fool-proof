FROM openjdk:17-jdk-alpine

# Run application as a non-root user.
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

CMD ["./gradlew", "clean", "build"]

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]