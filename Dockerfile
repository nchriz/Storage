
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew

RUN ./gradlew clean bootJar --no-daemon

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/Storage-0.0.1-SNAPSHOT.jar"]